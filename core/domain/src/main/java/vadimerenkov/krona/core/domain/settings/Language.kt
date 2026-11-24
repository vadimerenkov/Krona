package vadimerenkov.krona.core.domain.settings

enum class Language(
	val code: String,
	val endonym: String
) {
	ENGLISH("en-US", "English"),
	RUSSIAN("ru-RU", "Русский")
}