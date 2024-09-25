import os
import json
import logging
from collections import defaultdict
from colorama import Fore, Style, init
import re

init(autoreset=True)

INPUT_DIR = './inputFiles'
OUTPUT_DIR = './outputFiles'

logging.basicConfig(level=logging.INFO, format='%(message)s')
logger = logging.getLogger()

def read_json_files(directory):
    json_files = []
    for filename in os.listdir(directory):
        if filename.endswith('.json'):
            filepath = os.path.join(directory, filename)
            with open(filepath, 'r') as file:
                json_files.append((filename, json.load(file)))
    return json_files

def collect_rule_usage(json_content):
    rule_usage = defaultdict(int)

    # Convert JSON content to a single string
    json_string = json.dumps(json_content)

    # Extract all rule names from the JSON content
    rule_names = set()
    def find_rules(obj):
        if isinstance(obj, dict):
            for key, value in obj.items():
                if key == 'rules' and isinstance(value, list):
                    for rule in value:
                        if 'name' in rule:
                            rule_name = rule['name']
                            rule_names.add(rule_name)
                find_rules(value)
        elif isinstance(obj, list):
            for item in obj:
                find_rules(item)
    find_rules(json_content)

    # Count occurrences of each rule name in the JSON string
    for rule in rule_names:
        rule_pattern = re.compile(rf"\b{rule}\b")
        matches = rule_pattern.findall(json_string)
        rule_usage[rule] = len(matches)
        logger.debug(f"Found {len(matches)} matches for rule '{rule}'")

    return rule_usage

def find_unused_rules(rule_usage):
    unused_rules = set()
    reasons = {}

    for rule, count in rule_usage.items():
        if count == 2:  # Only consider rules with exactly 2 occurrences
            unused_rules.add(rule)
            reasons[rule] = f"Rule is not being used anywhere"

    return unused_rules, reasons

def remove_unused_rules(json_content, unused_rules):
    removed_rules = []

    def remove_rules(obj):
        if isinstance(obj, dict):
            for key, value in list(obj.items()):
                if key == 'rules' and isinstance(value, list):
                    for rule in value:
                        if rule.get('name') in unused_rules:
                            removed_rules.append(rule.get('name'))
                    obj[key] = [rule for rule in value if rule.get('name') not in unused_rules]
                else:
                    remove_rules(value)
        elif isinstance(obj, list):
            for item in obj:
                remove_rules(item)

    remove_rules(json_content)
    return removed_rules

def process_file(filename, json_content):
    # Store all removed rules across multiple runs
    all_removed_rules = set()
    all_reasons = {}

    # Run the algorithm until no more rules are removed
    while True:
        rule_usage = collect_rule_usage(json_content)
        unused_rules, reasons = find_unused_rules(rule_usage)
        if not unused_rules:
            break  # Exit loop if no more unused rules

        # Remove unused rules
        removed_rules = remove_unused_rules(json_content, unused_rules)
        all_removed_rules.update(removed_rules)
        all_reasons.update(reasons)

    # Write final output
    output_path = os.path.join(OUTPUT_DIR, filename)
    with open(output_path, 'w') as outfile:
        json.dump(json_content, outfile, indent=2)

    return all_removed_rules, all_reasons

def main():
    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)

    json_files = read_json_files(INPUT_DIR)

    for filename, json_content in json_files:
        logger.info(Fore.GREEN + f"\nProcessing file: {filename}")
        removed_rules, reasons = process_file(filename, json_content)
        
        # Log removed rules specific to this file
        if removed_rules:
            logger.info(Fore.YELLOW + f"\n{filename}")
            for rule in removed_rules:
                logger.info(Fore.RED + f"Removed rule: {rule} - Reason: {reasons[rule]}")
        else:
            logger.info(Fore.YELLOW + f"\n{filename}")
            logger.info("No rules removed")

if __name__ == '__main__':
    main()
