package com.feko.generictabletoprpg.com.feko.generictabletoprpg.common.filter

import com.feko.generictabletoprpg.filters.Filter
import com.feko.generictabletoprpg.filters.GenericFilter
import com.feko.generictabletoprpg.filters.SpellFilter
import com.feko.generictabletoprpg.spell.Spell

class FilterPredicate(val filter: Filter?) : (Any) -> Boolean {
    override fun invoke(item: Any): Boolean {
        if (filter == null) {
            return true
        }

        return when (filter) {
            is SpellFilter -> filterSpell(filter, item)
            is GenericFilter -> filterGeneric(filter, item)
        }
    }

    private fun filterGeneric(genericFilter: GenericFilter, item: Any): Boolean {
        return item::class.java == genericFilter.type
    }

    private fun filterSpell(spellFilter: SpellFilter, item: Any): Boolean {
        if (item !is Spell) {
            return false
        }

        spellFilter.school?.let { schoolFilter ->
            if (item.school.lowercase() != schoolFilter.lowercase()) {
                return false
            }
        }

        spellFilter.concentration?.let {
            if (item.concentration != it) {
                return false
            }
        }

        return true
    }
}
