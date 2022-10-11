package com.veryfi.android

enum class Constants(val value: String) {
    /**
     * header for HttpRequest
     */
    ACCEPT("Accept"),
    /**
     * header for HttpRequest
     */
    USER_AGENT("User-Agent"),

    /**
     * header for HttpRequest
     */
    USER_AGENT_KOTLIN("Kotlin Veryfi-Kotlin/1.0.4"),

    /**
     * header for HttpRequest
     */
    APPLICATION_JSON("application/json"),

    /**
     * header for HttpRequest
     */
    CONTENT_TYPE("Content-Type"),

    /**
     * header for HttpRequest
     */
    CLIENT_ID("Client-Id"),

    /**
     * header for HttpRequest
     */
    AUTHORIZATION("Authorization"),

    /**
     * header for HttpRequest
     */
    X_VERYFI_REQUEST_TIMESTAMP("X-Veryfi-Request-Timestamp"),

    /**
     * header for HttpRequest
     */
    X_VERYFI_REQUEST_SIGNATURE("X-Veryfi-Request-Signature"),

    /**
     * header for HttpRequest
     */
    TIMESTAMP("timestamp"),

    /**
     * header for HttpRequest
     */
    FILE_NAME("file_name"),

    /**
     * header for HttpRequest
     */
    FILE_DATA("file_data"),

    /**
     * header for HttpRequest
     */
    CATEGORIES ("categories"),

    /**
     * header for HttpRequest
     */
    AUTO_DELETE ("auto_delete"),

    /**
     * header for HttpRequest
     */
    BOOST_MODE("boost_mode"),

    /**
     * header for HttpRequest
     */
    EXTERNAL_ID ("external_id"),

    /**
     * header for HttpRequest
     */
    FILE_URL ("file_url"),

    /**
     * header for HttpRequest
     */
    FILE_URLS("file_urls"),

    /**
     * header for HttpRequest
     */
    MAX_PAGES_TO_PROCESS("max_pages_to_process")

}