# uploader script
A bash script to help with uploading content

## Dependency
Install these dependency in-order to use the uploader tool:
- [jq](https://formulae.brew.sh/formula/jq), it's a lightweight command-line JSON processor

## How to use it
- Make sure the script is executable. Use `ls -l` to check for permissions and `chmod +x <file-name>` to make it executable.

- Update the `config.txt` file with your credentials, resource folder name and base FHIR server url. See the [sample-config file](sample_config.txt) for examples

- On the command line run `bash uploader.sh`
- Output will be posted to a file named `output.txt` in the same folder

## Grant type option
Grant type decides how your device authenticate to the server in-order to receive an access token, so your device can access the server's resources.
This tool only supports 2 types of grant type at the moment:
1. `client_credentials`
2. `password`
