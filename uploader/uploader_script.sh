#!/bin/bash

# Initialize variables
SERVER_URL=""
CLIENT_ID=""
CLIENT_SECRET=""
USERNAME=""
PASSWORD=""
SOURCE=""
ACCESS_TOKEN=""
AUTH_SERVER_URL="https://example.com/oauth/token" #To-review

# Function to obtain an OAuth2 access token
get_access_token() {
    # Make a request to the OAuth2 token endpoint to obtain an access token
    # Replace AUTH_SERVER_URL, CLIENT_ID, CLIENT_SECRET, USERNAME, and PASSWORD with your values
    RESPONSE=$(curl -X POST -d "grant_type=password&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&username=$USERNAME&password=$PASSWORD" $AUTH_SERVER_URL)

    # Check if the response contains an error
    if echo "$RESPONSE" | jq -e 'has("error")' >/dev/null; then
        echo "Error: Failed to obtain an access token."
        echo "Response: $RESPONSE"
        exit 1
    fi

    # Parse the response to extract the access token
    ACCESS_TOKEN=$(echo "$RESPONSE" | jq -r '.access_token')
}

# Function to display usage instructions and exit
usage() {
    echo "Usage: $0 -s|--source <source_folder> -i|--client-id <client_id> -sec|--client-secret <client_secret> -u|--username <username> -p|--password <password> -url|--server-url <server_url>"
    exit 1
}

# Parse command-line arguments
while [[ "$#" -gt 0 ]]; do
    case "$1" in
        -s|--source)
            SOURCE="$2"
            shift 2
            ;;
        -i|--client-id)
                    CLIENT_ID="$2"
                    shift 2
                    ;;
        -sec|--client-secret)
            CLIENT_SECRET="$2"
            shift 2
            ;;
        -u|--username)
            USERNAME="$2"
            shift 2
            ;;
        -p|--password)
            PASSWORD="$2"
            shift 2
            ;;
        -url|--server-url)
            SERVER_URL="$2"
            shift 2
            ;;
        *)
            usage
            ;;
    esac
done

# Check if the required arguments are provided
if [ -z "$SOURCE" ] || [ -z "$CLIENT_ID" ] || [ -z "$CLIENT_SECRET" ] || [ -z "$USERNAME" ] || [ -z "$PASSWORD" ] || [ -z "$SERVER_URL" ]; then
    usage
fi

# Obtain an OAuth2 access token
get_access_token

# Check if the access token is empty
if [ -z "$ACCESS_TOKEN" ]; then
    echo "Failed to obtain an access token."
    exit 1
fi

# File upload logic using curl
if [ -d "$SOURCE" ]; then
    # Extract the folder name from the path
    RESOURCE_TYPE=$(basename "$SOURCE")

    # Iterate through the files in the folder and upload each one
    for FILE_PATH in "$SOURCE"/*; do
        if [ -f "$FILE_PATH" ]; then
            # Use curl to upload each file content with basic authentication
            curl -u "$USERNAME:$PASSWORD" -X POST -F "file=@$FILE_PATH" -F "resource_type=$RESOURCE_TYPE" $SERVER_URL

            # Check the response code to ensure the upload was successful
            RESPONSE_CODE=$?
            if [ $RESPONSE_CODE -eq 0 ]; then
                echo "File uploaded successfully: $FILE_PATH"
            else
                echo "File upload failed with response code: $RESPONSE_CODE for file: $FILE_PATH"
            fi
        fi
    done
else
    echo "Source not found or is not a folder: $SOURCE"
    exit 1
fi
