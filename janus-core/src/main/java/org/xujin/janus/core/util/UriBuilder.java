package org.xujin.janus.core.util;

import io.netty.util.internal.StringUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author: gan
 * @date: 2020/4/23
 */
public class UriBuilder {
    private transient String scheme;            // null ==> relative URI
    // Components of all URIs: [<scheme>:]<scheme-specific-part>[#<fragment>]private transient String scheme;            // null ==> relative URI
    private transient String fragment;

    // Hierarchical URI components: [//<authority>]<path>[?<query>]
    private transient String authority;         // Registry or server

    // Server-based authority: [<userInfo>@]<host>[:<port>]
    private transient String userInfo;
    private transient String host;              // null ==> registry-based
    private transient int port = -1;            // -1 ==> undefined

    // Remaining components of hierarchical URIs
    private transient String path;              // null ==> opaque
    private transient String query;

    // The remaining fields may be computed on demand

    private volatile transient String schemeSpecificPart;
    private volatile transient int hash;        // Zero ==> undefined

    private volatile transient String decodedUserInfo = null;
    private volatile transient String decodedAuthority = null;
    private volatile transient String decodedPath = null;
    private volatile transient String decodedQuery = null;
    private volatile transient String decodedFragment = null;
    private volatile transient String decodedSchemeSpecificPart = null;


    public static UriBuilder from(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not null");
        }
        UriBuilder uriBuilder = new UriBuilder();
        uriBuilder.scheme = uri.getScheme();
        uriBuilder.fragment = uri.getRawFragment();
        uriBuilder.authority = uri.getAuthority();
        uriBuilder.userInfo = uri.getRawUserInfo();
        uriBuilder.decodedUserInfo = uri.getUserInfo();
        uriBuilder.host = uri.getHost();
        uriBuilder.port = uri.getPort();
        uriBuilder.path = uri.getPath();
        uriBuilder.query = uri.getRawQuery();
        uriBuilder.schemeSpecificPart = uri.getSchemeSpecificPart();
        return uriBuilder;
    }

    public UriBuilder replacePath(String path) {
        this.path = path;
        return this;
    }

    public UriBuilder replaceHost(String host) {
        this.host = host;
        return this;
    }

    public UriBuilder replacePort(int port) {
        this.port = port;
        return this;
    }

    public UriBuilder replaceScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public URI build() {

        StringBuilder uriBuilder = new StringBuilder();
        if (this.scheme != null) {
            uriBuilder.append(this.scheme).append(':');
        }
        if (this.userInfo != null || this.host != null) {
            uriBuilder.append("//");
            if (this.userInfo != null) {
                uriBuilder.append(this.userInfo).append('@');
            }
            if (this.host != null) {
                uriBuilder.append(this.host);
            }
            if (this.port != -1) {
                uriBuilder.append(':').append(this.port);
            }
        }
        if (!StringUtil.isNullOrEmpty(this.path)) {
            uriBuilder.append(this.path);
        }
        if (this.query != null) {
            uriBuilder.append('?').append(this.query);
        }
        if (this.fragment != null) {
            uriBuilder.append('#').append(this.fragment);
        }
        try {
            return new URI(uriBuilder.toString());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Invalid URI string: \"" + uriBuilder.toString() + "\"", ex);
        }
    }


}
