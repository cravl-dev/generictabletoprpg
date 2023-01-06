package com.feko.generictabletoprpg.import

import com.feko.generictabletoprpg.ammunition.Ammunition
import com.feko.generictabletoprpg.ammunition.InsertAmmunitionsPort
import com.feko.generictabletoprpg.common.Logger

@Suppress("UNCHECKED_CAST")
class OrcbrewImportAmmunitionsUseCaseImpl(
    private val processEdnMapPort: ProcessEdnMapPort,
    private val insertAmmunitionsPort: InsertAmmunitionsPort,
    private val logger: Logger

) : OrcbrewImportAmmunitionsUseCase {
    override fun import(sources: Map<Any, Any>): Result<Boolean> {
        val ammunitionsToAdd = mutableListOf<Ammunition>()
        val exceptions = mutableListOf<Exception>()
        sources.forEach { source ->
            val content = source.value as Map<Any, Any>
            val ammunitionsKey = ":orcpub.dnd.e5/ammunitions"
            val hasValueForKey = processEdnMapPort.containsKey(content, ammunitionsKey)
            if (hasValueForKey) {
                val ammunitions = processEdnMapPort.getValue<Map<Any, Any>>(content, ammunitionsKey)
                ammunitions.forEach { ammunition ->
                    try {
                        val ammunitionMap = ammunition.value as Map<Any, Any>
                        val ammunitionToAdd = Ammunition.createFromOrcbrewData(
                            processEdnMapPort,
                            ammunitionMap,
                            source.key.toString()
                        )
                        ammunitionsToAdd.add(ammunitionToAdd)
                    } catch (e: Exception) {
                        logger.error(e, "Failed to process ammunition named '${ammunition.key}.")
                        exceptions.add(e)
                    }
                }
            }
        }
        if (exceptions.isNotEmpty() and
            ammunitionsToAdd.isNotEmpty()
        ) {
            return Result.success(false)
        }
        if (ammunitionsToAdd.isEmpty()) {
            return Result.success(true)
        }
        return insertAmmunitionsPort.insertAll(ammunitionsToAdd)
    }
}