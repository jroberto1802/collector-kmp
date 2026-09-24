package com.softcom.collector.model

enum class CollectionFilter {
    RECENTES,
    TODAS,
    ARQUIVADAS,
}

data class Collection(
    val id: Long,
    val name: String,
    val status: String,
    val archived: Boolean,
    val itemCount: Int,
    val createdAt: Long,
)

data class CollectionsEmptyCopy(
    val title: String,
    val description: String,
)

const val DEFAULT_COLLECTION_STATUS = "Não sincronizado"
const val SEVEN_DAYS_MS = 7L * 24L * 60L * 60L * 1000L

fun emptyCopyFor(filter: CollectionFilter): CollectionsEmptyCopy = when (filter) {
    CollectionFilter.ARQUIVADAS -> CollectionsEmptyCopy(
        title = "Nenhuma coleta arquivada!",
        description = "As coletas arquivadas aparecerão aqui.",
    )
    CollectionFilter.TODAS -> CollectionsEmptyCopy(
        title = "Nenhuma coleta encontrada!",
        description = "Toque no botão (+) para realizar uma nova coleta",
    )
    CollectionFilter.RECENTES -> CollectionsEmptyCopy(
        title = "Nenhuma coleta recente!",
        description = "Toque no botão (+) para realizar uma nova coleta",
    )
}
