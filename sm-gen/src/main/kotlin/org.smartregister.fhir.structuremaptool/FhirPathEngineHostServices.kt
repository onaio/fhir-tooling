package org.smartregister.fhir.structuremaptool


import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.TypeDetails
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.FHIRPathEngine

/*
* Resolves constants defined in the fhir path expressions beyond those defined in the specification
*/
internal object FHIRPathEngineHostServices : FHIRPathEngine.IEvaluationContext {
    override fun resolveConstant(appContext: Any?, name: String?, beforeContext: Boolean): Base? =
        (appContext as? Map<*, *>)?.get(name) as? Base

    override fun resolveConstantType(appContext: Any?, name: String?): TypeDetails {
        throw UnsupportedOperationException()
    }

    override fun log(argument: String?, focus: MutableList<Base>?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun resolveFunction(
        functionName: String?
    ): FHIRPathEngine.IEvaluationContext.FunctionDetails {
        throw UnsupportedOperationException()
    }

    override fun checkFunction(
        appContext: Any?,
        functionName: String?,
        parameters: MutableList<TypeDetails>?
    ): TypeDetails {
        throw UnsupportedOperationException()
    }

    override fun executeFunction(
        appContext: Any?,
        focus: MutableList<Base>?,
        functionName: String?,
        parameters: MutableList<MutableList<Base>>?
    ): MutableList<Base> {
        throw UnsupportedOperationException()
    }

    override fun resolveReference(appContext: Any?, url: String?): Base {
        throw UnsupportedOperationException()
    }

    override fun conformsToProfile(appContext: Any?, item: Base?, url: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun resolveValueSet(appContext: Any?, url: String?): ValueSet {
        throw UnsupportedOperationException()
    }
}