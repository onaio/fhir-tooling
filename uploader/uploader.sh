#!/bin/bash

push_to_server() {
    # Get the resource type
    RESOURCE_TYPE=$(cat "$@" | jq -r '.resourceType')

    # Post to server
    ENDPOINT_URL="${SERVER_URL}/${RESOURCE_TYPE}"
    echo -e '\n'
    echo Resource File: "$@"
    echo Endpoint: "$ENDPOINT_URL"

    # Make the POST request
    RESPONSE=$(curl -i -X POST "$ENDPOINT_URL" -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/fhir+json" -d @"$@" --silent)
    echo "$RESPONSE" >> response.txt

    # Extract the location header
    LOCATION_HEADER=$(echo "$RESPONSE" | awk '/^location:/ { print $0 }')
    echo "Location Header: $LOCATION_HEADER"

    # Extract the generated resource ID
    GENERATED_ID=$(echo "$LOCATION_HEADER" | sed -n 's|.*/\([^/]*\)/_history.*|\1|p')
    echo "Extracted Generated ID: $GENERATED_ID"

    if [ ! -z "$GENERATED_ID" ]; then
        # Update the resource ID in the file
        jq --arg id "$GENERATED_ID" '.id = $id' "$@" > tmp.$$.json && mv tmp.$$.json "$@"
        echo "Updated the resource ID in $@ to $GENERATED_ID."
    else
        echo "Failed to obtain the generated ID from the server."
    fi
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
