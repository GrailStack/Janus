package org.xujin.janus.filter.filters;

import com.google.gson.Gson;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.filter.filters.AbstractAsyncFilter;
import org.xujin.janus.core.util.AsyncHttpRequestHelper;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import org.xujin.janus.registry.RegistryService;
import org.xujin.janus.registry.RegistryServiceRepo;
import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.loadbalancer.LoadBalancer;
import org.xujin.janus.registry.loadbalancer.LoadBalancerFactory;
import org.xujin.janus.registry.loadbalancer.LoadBalancerParam;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author: gan
 * @date: 2020/6/10
 */
public class AuthTokenFilter extends AbstractAsyncFilter {

    @Override
    public void doFilter(FilterContext context) {
        AuthTokenConfig authTokenConfig = new AuthTokenConfig(getConfig(context));
        FullHttpRequest originHttpRequest = context.getCtx().getOriFullHttpRequest();
        String token = originHttpRequest.headers().get(authTokenConfig.getTokenHeaderKey());
        if (token == null) {
            throw new RuntimeException("expect " + authTokenConfig.getTokenHeaderKey() + " in header for token check");
        }
        String serverAddress = lookUpServerAddress(authTokenConfig.getTokenServiceName(), authTokenConfig);
        getTokenInfo(token, serverAddress, authTokenConfig, httpResponse -> {
            FullHttpResponse fullHttpResponse = (FullHttpResponse) httpResponse;
            TokenResponse tokenResponse = readHttpResponse(fullHttpResponse);
            if (tokenResponse == null) {
                sendResponse(context, "get token info fail", HttpResponseStatus.SERVICE_UNAVAILABLE);
                return;
            }
            if (tokenResponse.getMsgCode() != 200) {
                sendResponse(context, tokenResponse.getMsgContent(), HttpResponseStatus.UNAUTHORIZED);
                return;
            }
            originHttpRequest.headers().set("X-JANUS-UserId", tokenResponse.getData().getUserId());
            originHttpRequest.headers().set("X-JANUS-UserName", tokenResponse.getData().getUserName());
            complete(context);

        }, throwable -> {
            sendResponse(context, throwable.getMessage(), HttpResponseStatus.SERVICE_UNAVAILABLE);
        });
    }


    @Override
    public int order() {
        return 10;
    }

    @Override
    public FilterType type() {
        return FilterType.INBOUND;
    }

    private void getTokenInfo(String token, String serverAddress, AuthTokenConfig authTokenConfig,
                              Consumer<HttpResponse> completeSupplier, Consumer<Throwable> errorSupplier) {
        String uri = authTokenConfig.getTokenServiceProtocol() + "://" + serverAddress + authTokenConfig.getTokenServicePath();
        DefaultJanusCtx defaultJanusCtx = new DefaultJanusCtx();
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
        httpRequest.headers().set(authTokenConfig.getTokenHeaderKey(), token);
        httpRequest.headers().set(HttpHeaderNames.HOST, serverAddress);
        defaultJanusCtx.setOriFullHttpRequest(httpRequest);
        AsyncHttpRequestHelper.asyncSendHttpRequest(defaultJanusCtx, httpResponse -> {
            completeSupplier.accept(httpResponse);

        }, throwable -> {
            errorSupplier.accept(throwable);
        });
        ReferenceCountUtil.release(httpRequest);
    }

    private void sendResponse(FilterContext context, String errorMsg, HttpResponseStatus responseStatus) {
        context.setTextResponse("token check error,message:" + errorMsg, responseStatus);
        context.getCtx().janusToOutsideClientAsyncSend(context.getCtx().getOriHttpResponse());
    }


    private TokenResponse readHttpResponse(FullHttpResponse httpResponse) {
        byte[] data = new byte[httpResponse.content().capacity()];
        httpResponse.content().readBytes(data);
        String str = new String(data, StandardCharsets.UTF_8);
        TokenResponse tokenResponse = new Gson().fromJson(str, TokenResponse.class);
        return tokenResponse;
    }

    private String lookUpServerAddress(String serviceName, AuthTokenConfig authTokenConfig) {
        LoadBalancer loadBalancer = LoadBalancerFactory.create(authTokenConfig.getLoadBalanceName());
        RegistryService serviceDiscovery = RegistryServiceRepo.getRegistryService();
        List<ServerNode> serverNodes = serviceDiscovery.getServerNode(serviceName);
        LoadBalancerParam loadBalancerParam = new LoadBalancerParam(null, null);
        ServerNode serverNode = loadBalancer.select(serverNodes, loadBalancerParam);
        if (serverNode == null) {
            throw new RuntimeException("no server found for " + serviceName);
        }
        return serverNode.getHost() + ":" + serverNode.getPort();
    }

    private class TokenResponse {
        private Integer code;
        private UserInfo data;
        private Integer msgCode;
        private String msgContent;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public UserInfo getData() {
            return data;
        }

        public void setData(UserInfo data) {
            this.data = data;
        }

        public Integer getMsgCode() {
            return msgCode;
        }

        public void setMsgCode(Integer msgCode) {
            this.msgCode = msgCode;
        }

        public String getMsgContent() {
            return msgContent;
        }

        public void setMsgContent(String msgContent) {
            this.msgContent = msgContent;
        }
    }

    private class UserInfo {
        private String userId;
        private String userName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    private class AuthTokenConfig {
        private String loadBalanceName = LoadBalancerFactory.ROUND_ROBIN_NAME;
        private String tokenServiceProtocol = "http";
        private String tokenServiceName = "login-auth";
        private String tokenServicePath = "/uc/ctoken/getInfoFromToken";
        private String tokenHeaderKey = HttpHeaderNames.AUTHORIZATION.toString();

        public AuthTokenConfig(Map<String, String> config) {
            if (config != null || config.size() > 0) {
                Optional.ofNullable(config.get("loadBalanceName")).ifPresent(this::setLoadBalanceName);
                Optional.ofNullable(config.get("tokenServiceProtocol")).ifPresent(this::setTokenServiceProtocol);
                Optional.ofNullable(config.get("tokenServiceName")).ifPresent(this::setTokenServiceName);
                Optional.ofNullable(config.get("tokenServicePath")).ifPresent(this::setTokenServicePath);
                Optional.ofNullable(config.get("tokenHeaderKey")).ifPresent(this::setTokenHeaderKey);
            }
        }

        public String getLoadBalanceName() {
            return loadBalanceName;
        }

        public void setLoadBalanceName(String loadBalanceName) {
            this.loadBalanceName = loadBalanceName;
        }

        public String getTokenServiceProtocol() {
            return tokenServiceProtocol;
        }

        public void setTokenServiceProtocol(String tokenServiceProtocol) {
            this.tokenServiceProtocol = tokenServiceProtocol;
        }

        public String getTokenServiceName() {
            return tokenServiceName;
        }

        public void setTokenServiceName(String tokenServiceName) {
            this.tokenServiceName = tokenServiceName;
        }

        public String getTokenServicePath() {
            return tokenServicePath;
        }

        public void setTokenServicePath(String tokenServicePath) {
            this.tokenServicePath = tokenServicePath;
        }

        public String getTokenHeaderKey() {
            return tokenHeaderKey;
        }

        public void setTokenHeaderKey(String tokenHeaderKey) {
            this.tokenHeaderKey = tokenHeaderKey;
        }
    }
}
