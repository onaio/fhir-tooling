# Linking Duplicate Patient Resources

This script takes in the IDs of duplicate patient resources and links them using the `patient.link` attribute

## Setup
1. Navigate to the `linking` directory
   ```
   cd .../linking
   ```
2. Create and activate a virtual environment
   ```
   virtualenv venv
   source venv/bin/activate
   ```
3. Install dependencies
   ```
   pip install -r requirements.txt
   ```
4. Update the `config.py` file with the correct credentials to access your server
   
## Usage
- To run the script you need to pass in the IDs of two patient resource. For example
   ```
  python3 main.py --patient_ids "12345678|91011121"
   ```