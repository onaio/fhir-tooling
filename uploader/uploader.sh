#!/bin/bash

push_to_server() {
    # Get the resource type
    RESOURCE_TYPE=$(cat "$@" | jq '.resourceType')
    RESOURCE_TYPE="${RESOURCE_TYPE:1:-1}"

    # Post to server
    SERVER_URL="$SERVER_URL/${RESOURCE_TYPE}"
    echo -e '\n'
    echo Resource File: "$@"
    echo Endpoint: "$SERVER_URL"
    curl --write-out "%{http_code}\n" -X POST $SERVER_URL -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/fhir+json" -d @"$@" --silent >> output.txt
}

main() {
    # Import configs
    . config.txt

    # Get access_token
    RESPONSE=$(curl -X POST -d "grant_type=password&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&username=$USERNAME&password=$PASSWORD" $ACCESS_TOKEN_URL)
    if echo "$RESPONSE" | jq -e 'has("error")' >/dev/null; then
        echo "Error: Failed to obtain an access token."
        echo "Response: $RESPONSE"
        exit 1
    fi

    # Parse the response to extract the access token
    ACCESS_TOKEN=$(jq -r '.access_token' <<< $RESPONSE)
    export ACCESS_TOKEN
    export SERVER_URL
    # Get the files in the resource folder and push to server
    find $RESOURCE_FOLDER -type f -exec bash -c 'push_to_server "$@"' bash {} \;
}

export -f push_to_server
main
