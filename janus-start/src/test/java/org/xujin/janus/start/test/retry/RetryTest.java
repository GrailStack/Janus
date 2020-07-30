package org.xujin.janus.start.test.retry;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.resilience.retry.RetryRepo;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.github.resilience4j.retry.Retry;
import io.netty.handler.codec.http.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.xujin.janus.startup.JanusBootStrap;

import java.util.function.Predicate;

/**
 * @author: gan
 * @date: 2020/5/15
 */
public class RetryTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @After
    public void destroy() {
        JanusBootStrap.destroy();
    }

    @Test
    public void testRetryInit() {
        Assert.assertTrue(RetryRepo.getAllRetry().size() > 0);
    }

    @Test
    public void testRetryConfig() {
        Retry retry = RetryRepo.get("RetryGetPostTest");
        Assert.assertTrue(retry.getRetryConfig().getMaxAttempts() == 5);
        Assert.assertTrue(retry.getRetryConfig().getIntervalFunction() != null);
        Assert.assertTrue(retry.getRetryConfig().getResultPredicate() != null);
    }

    @Test
    public void testResultPredicate_null_false() {
        Retry retry = RetryRepo.get("RetryGetPostTest");
        FilterContext context = new FilterContext(null);
        Assert.assertFalse(retry.context().onResult(context));
    }

    @Test
    public void testPredicate_statusCode() {
        String statusCode = "500";
        Predicate<FilterContext> filterContextPredicate = filterContext -> filterContext.getCtx()
                .getOriHttpResponse().status().codeAsText().toString().equalsIgnoreCase(statusCode);
        FilterContext context = getFilterContext(HttpMethod.GET, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        Assert.assertTrue(filterContextPredicate.test(context));
    }

    @Test
    public void testPredicate_method_or() {
        String methodGet = "Get";
        String methodPost = "Post";
        Predicate<FilterContext> filterContextPredicate = filterContext -> filterContext.getCtx()
                .getOriFullHttpRequest().method().name().equalsIgnoreCase(methodGet);
        Predicate<FilterContext> filterContextPredicate1 = filterContext -> filterContext.getCtx()
                .getOriFullHttpRequest().method().name().equalsIgnoreCase(methodPost);
        FilterContext context = getFilterContext(HttpMethod.GET, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        filterContextPredicate = filterContextPredicate.or(filterContextPredicate1);
        Assert.assertTrue(filterContextPredicate.test(context));
    }

    @Test
    public void testPredicate_method_or_statusCode_and() {
        String methodGet = "Get";
        String methodPost = "Post";
        String statusCode = "200";
        String statusCode1 = "300";
        Predicate<FilterContext> resultPredicate = filterContext -> filterContext.getCtx() != null &&
                filterContext.getCtx().getOriHttpResponse() != null;
        Predicate<FilterContext> filterContextPredicate = filterContext -> filterContext.getCtx()
                .getOriFullHttpRequest().method().name().equalsIgnoreCase(methodGet);
        Predicate<FilterContext> filterContextPredicate1 = filterContext -> filterContext.getCtx()
                .getOriFullHttpRequest().method().name().equalsIgnoreCase(methodPost);
        Predicate<FilterContext> filterContextPredicate2 = filterContext -> filterContext.getCtx()
                .getOriHttpResponse().status().codeAsText().toString().equalsIgnoreCase(statusCode);
        Predicate<FilterContext> filterContextPredicate3 = filterContext -> filterContext.getCtx()
                .getOriHttpResponse().status().codeAsText().toString().equalsIgnoreCase(statusCode1);
        FilterContext context = getFilterContext(HttpMethod.GET, HttpResponseStatus.INTERNAL_SERVER_ERROR);

        resultPredicate = resultPredicate.and(filterContextPredicate.or(filterContextPredicate1));
        resultPredicate = resultPredicate.and(filterContextPredicate2.or(filterContextPredicate3));
        Assert.assertFalse(resultPredicate.test(context));
    }

    @Test
    public void testResultPredicate_Get_200_false() {
        Retry retry = RetryRepo.get("RetryGetPostTest");
        FilterContext context = getFilterContext(HttpMethod.GET, HttpResponseStatus.OK);
        Assert.assertFalse(retry.context().onResult(context));
    }

    @Test
    public void testResultPredicate_Get_500_true() {
        Retry retry = RetryRepo.get("RetryGetPostTest");
        FilterContext context = getFilterContext(HttpMethod.GET, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        Assert.assertTrue(retry.context().onResult(context));
    }
    @Test
    public void testResultPredicate_Post_500_error() {
        Retry retry = RetryRepo.get("RetryGetPostTest");
        FilterContext context = getFilterContext(HttpMethod.POST, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        Assert.assertTrue(retry.context().onResult(context));
    }

    private FilterContext getFilterContext(HttpMethod httpMethod, HttpResponseStatus httpResponseStatus) {
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, "");
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        DefaultJanusCtx defaultJanusCtx = new DefaultJanusCtx();
        defaultJanusCtx.setOriFullHttpRequest(fullHttpRequest);
        defaultJanusCtx.setOriHttpResponse(fullHttpResponse);
        FilterContext context = new FilterContext(defaultJanusCtx);
        return context;

    }
}
