package org.smartregister.fct.workflow.util

import okio.Path
import okio.Path.Companion.toPath
import org.smartregister.fct.engine.util.capitalizeWords
import org.smartregister.fct.fm.util.FileUtil
import org.smartregister.fct.workflow.domain.model.Workflow
import java.io.File

internal object WorkflowConfig {

    var activeWorkflow: Workflow? = null

    private val WORKFLOW_FILE_PATH =
        "${FileUtil.PRIVATE_ROOT}${File.separator}workflow${File.separator}"

    fun getWorkflowPath(workflowId: String): String {
        return "$WORKFLOW_FILE_PATH$workflowId${File.separator}"
    }

    fun getPlanDefinitionPath(workflowId: String): Path {
        return "${getWorkflowPath(workflowId)}${File.separator}plan_definition.json".toPath()
    }

    fun getSubjectPath(workflowId: String): Path {
        return "${getWorkflowPath(workflowId)}${File.separator}subject.json".toPath()
    }

    fun getFileName(path: String): String {
        return FileUtil.getFilename(path).replace("_", " ").capitalizeWords()
    }

    const val SAMPLE_SUBJECT = """{
    "resourceType": "Patient",
    "id": "example",
    "identifier": [
        {
            "use": "usual",
            "type": {
                "coding": [
                    {
                        "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
                        "code": "MR"
                    }
                ]
            },
            "system": "urn:oid:1.2.36.146.595.217.0.1",
            "value": "12345",
            "period": {
                "start": "2001-05-06"
            },
            "assigner": {
                "display": "Acme Healthcare"
            }
        }
    ],
    "active": true,
    "name": [
        {
            "use": "official",
            "family": "Chalmers",
            "given": [
                "Peter",
                "James"
            ]
        },
        {
            "use": "usual",
            "given": [
                "Jim"
            ]
        },
        {
            "use": "maiden",
            "family": "Windsor",
            "given": [
                "Peter",
                "James"
            ],
            "period": {
                "end": "2002"
            }
        }
    ],
    "telecom": [
        {
            "use": "home"
        },
        {
            "system": "phone",
            "value": "(03) 5555 6473",
            "use": "work",
            "rank": 1
        },
        {
            "system": "phone",
            "value": "(03) 3410 5613",
            "use": "mobile",
            "rank": 2
        },
        {
            "system": "phone",
            "value": "(03) 5555 8834",
            "use": "old",
            "period": {
                "end": "2014"
            }
        }
    ],
    "gender": "male",
    "birthDate": "1974-12-25",
    "_birthDate": {
        "extension": [
            {
                "url": "http://hl7.org/fhir/StructureDefinition/patient-birthTime",
                "valueDateTime": "1974-12-25T14:35:45-05:00"
            }
        ]
    },
    "deceasedBoolean": false,
    "address": [
        {
            "use": "home",
            "type": "both",
            "text": "534 Erewhon St PeasantVille, Rainbow, Vic  3999",
            "line": [
                "534 Erewhon St"
            ],
            "city": "PleasantVille",
            "district": "Rainbow",
            "state": "Vic",
            "postalCode": "3999",
            "period": {
                "start": "1974-12-25"
            }
        }
    ],
    "contact": [
        {
            "relationship": [
                {
                    "coding": [
                        {
                            "system": "http://terminology.hl7.org/CodeSystem/v2-0131",
                            "code": "N"
                        }
                    ]
                }
            ],
            "name": {
                "family": "du Marché",
                "_family": {
                    "extension": [
                        {
                            "url": "http://hl7.org/fhir/StructureDefinition/humanname-own-prefix",
                            "valueString": "VV"
                        }
                    ]
                },
                "given": [
                    "Bénédicte"
                ]
            },
            "telecom": [
                {
                    "system": "phone",
                    "value": "+33 (237) 998327"
                }
            ],
            "address": {
                "use": "home",
                "type": "both",
                "line": [
                    "534 Erewhon St"
                ],
                "city": "PleasantVille",
                "district": "Rainbow",
                "state": "Vic",
                "postalCode": "3999",
                "period": {
                    "start": "1974-12-25"
                }
            },
            "gender": "female",
            "period": {
                "start": "2012"
            }
        }
    ],
    "managingOrganization": {
        "reference": "Organization/1"
    }
}
    """
}