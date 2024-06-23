package utils

data class GetTestData<T>(
    val yaml: String,
    val value: T,
)
