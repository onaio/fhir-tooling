#!/bin/bash

push_to_server() {
    # Get the resource type
    RESOURCE_TYPE=$(cat "$@" | jq -r '.resourceType')

    # Post to server
    SERVER_URL="$SERVER_URL/${RESOURCE_TYPE}"
    echo -e '\n'
    echo Resource File: "$@"
    echo Endpoint: "$SERVER_URL"
    curl --write-out "%{http_code}\n" -X POST $SERVER_URL -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/fhir+json" -d @"$@" --silent >> output.txt
}

main() {
    # Check if jq is installed
    command -v jq >/dev/null 2>&1 || { echo >&2 "Error: 'jq' is required but it's not installed. Aborting."; exit 1; }

    # Import configs
    . config.txt

    # Get access_token
    echo "Requesting access token..."

    if [ "$GRANT_TYPE" = "password" ]; then
        RESPONSE=$(curl -X POST -d "grant_type=password&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&username=$USERNAME&password=$PASSWORD" $ACCESS_TOKEN_URL)
    elif [ "$GRANT_TYPE" = "client_credentials" ]; then
        RESPONSE=$(curl -X POST -d "grant_type=client_credentials&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET" $ACCESS_TOKEN_URL)
    else
        echo "Error: Unsupported grant type: $GRANT_TYPE"
        exit 1
    fi

    if echo "$RESPONSE" | jq -e 'has("error")' >/dev/null; then
        echo "Error: Failed to obtain an access token."
        echo "Response: $RESPONSE"
        exit 1
    fi

    # Parse the response to extract the access token
    ACCESS_TOKEN=$(jq -r '.access_token' <<< $RESPONSE)
    export ACCESS_TOKEN
    export SERVER_URL

    echo "Access token obtained successfully."

    # Get the files in the resource folder and push to server
    find $RESOURCE_FOLDER -type f -exec bash -c 'push_to_server "$@"' bash {} \;
}

export -f push_to_server
main
