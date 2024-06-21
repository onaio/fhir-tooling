# Generate StrucureMap

---

### Prerequisites

- Setup and be able to run efsity

### Required
In order to generate a structureMap you need to provide the following
- Questionnaire
- Questionnaire Response
- Excel sheet with the configurations

To initiate the process run the command

`fct generateStructureMap -q /path-to-questionnaire file -qr /path-to-questionnaire-response file -c /path-to-excel-with-configs`

### Options
```text
-q  or --questionnaireResponsePath (Required)
-qr or --questionnaireResponsePath (Required)
-c  or --configPath (Required)
```

### Example Questionnaire
```json
{
  "resourceType": "Questionnaire",
  "language": "en",
  "status": "active",
  "publisher": "ONA-Systems",
  "subjectType": [
    "Group",
    "List"
  ],
  "extension": [
    {
      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap",
      "valueCanonical": "https://fhir.labs.smartregister.org/StructureMap/12345"
    }
  ],
  "id": "aa1b34de-f9d1-43f1-a25a-c6e9d993fa61",
  "item": [
    {
      "linkId": "questionnaire-field-name",
      "type": "string",
      "text": "Product name",
      "required": true
    },
    {
      "linkId": "questionnaire-field-quantity",
      "type": "integer",
      "text": "Quantity",
      "required": true
    },
    {
      "linkId": "questionnaire-field-date",
      "type": "dateTime",
      "text": "Date of delivery",
      "required": true
    },
    {
      "linkId": "questionnaire-field-location",
      "type": "string",
      "text": "Location",
      "required": true
    }
  ]
}
```

### Example QuestionnaireResponse
```json
{
  "resourceType": "QuestionnaireResponse",
  "id": "ec1c58d3-6367-4cfd-a3b2-45810f49e04e",
  "questionnaire": "Questionnaire/aa1b34de-f9d1-43f1-a25a-c6e9d993fa61",
  "status": "in-progress",
  "item": [
    {
      "linkId": "questionnaire-field-name",
      "text": "Product name",
      "answer": [
        {
          "valueString": "Westlands - Bednet Inventory"
        }
      ]
    },
    {
      "linkId": "questionnaire-field-quantity",
      "text": "Quantity",
      "answer": [
        {
          "valueInteger": 15
        }
      ]
    },
    {
      "linkId": "questionnaire-field-date",
      "text": "Date of delivery",
      "answer": [
        {
          "valueDateTime": "2024-06-04T06:11:00+00:00"
        }
      ]
    },
    {
      "linkId": "questionnaire-field-location",
      "text": "Location",
      "answer": [
        {
          "valueString": "Westlands"
        }
      ]
    }
  ]
}
```


### Example Excel Configs

| Questionnaire Response Field Id | Resource | Field path | Conversion | FHIR Path/StructureMap functions |
|---------------------------------| --- | --- | --- | --- |
| questionnaire-field-name        | List |
| questionnaire-field-quantity    | List |
| questionnaire-field-date        | List |
| questionnaire-field-location    | List |



### Result Sample StructureMap
```text
map "http://fhir.labs.smartregister.org/fhir/StructureMap/5875" = 'LinkEntity'

uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireReponse" as source
uses "http://hl7.org/fhir/StructureDefinition/Bundle" as target

group LinkEntity(source src : QuestionnaireResponse, target bundle : Bundle) {
    src -> bundle.type = 'collection' "r_bundle_type";
    src -> bundle.entry as entry, entry.resource = create('List') as list then
        ExtractList(src, list) "r_bundle_entries";
}

group ExtractList(source src : QuestionnaireResponse, target list : List){
    src -> list.id = uuid() "r_list_id";
    src -> list.status = 'current' "r_list_status";
    src -> list.title = evaluate(src, $this.item.where(linkId = 'name').answer.value) "r_list_title";
    
    src -> list.code = create('CodeableConcept') as list_code then {
            src -> list_code.coding = create('Coding') as coding then {
                src -> coding.system = 'http://smartregister.org/' "r_list_code_coding_system";
                src -> coding.code = '22138876' "r_list_code_coding_code";
                src -> coding.display = 'Supply Inventory List' "r_list_code_coding_display";
            } "r_list_code_coding";
            src -> list_code.text = 'Supply Inventory List' "r_list_code-text";
        } "r_list_code";
        
    src -> list.subject = create("Reference") as ref then {
                src ->ref.reference = evaluate(src, "Location/"+ $this.item.where(linkId="location-id").answer.value) "r_enc_subject_ref";
            }  "r_enc_subject";
            
    src -> list.entry = create("List_Entry") as entry then {
            src -> entry.flag = create('CodeableConcept') as flag then {
                 src -> flag.coding = create('Coding') as coding then {
                     src -> coding.system = 'http://smartregister.org/' "r_flag_coding_system";
                     src -> coding.code = '22138876' "r_flag_coding_code";
                     src -> coding.display = 'Supply Inventory List' "r_flag_coding_display";
                 } "r_flag_coding";
                 src -> flag.text = 'Supply Inventory List' "r_flag-text";
            } "r_flag";
            src -> entry.date = evaluate(src, now()) "r_entry_date";
            src -> entry.item = create('Reference') as reference then {
                src -> reference.reference = evaluate(src, 'Group/' + inventoryProductId ) "r_entry_item_ref_ref";
            } "r_entry_item_ref";
        } "r_entry";
   
}
```
