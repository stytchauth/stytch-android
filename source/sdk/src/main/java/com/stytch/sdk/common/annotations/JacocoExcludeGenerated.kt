package com.stytch.sdk.common.annotations

// https://stackoverflow.com/questions/73169311/exclude-kotlin-data-classes-from-jacoco-test-coverage
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
)
internal annotation class JacocoExcludeGenerated
