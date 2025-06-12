import requests
import yaml



# Your GitHub repo and personal access token
REPO = "SimonLucas/planet-wars-rts-submissions"
TOKEN = "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"  # Use environment variable for safety!

HEADERS = {
    "Authorization": f"token {TOKEN}",
    "Accept": "application/vnd.github+json",
}

def get_open_issues():
    url = f"https://api.github.com/repos/{REPO}/issues"
    response = requests.get(url, headers=HEADERS)
    response.raise_for_status()
    return response.json()

import re
import yaml

def parse_yaml_from_issue_body(body):
    try:
        # Use regex to extract the yaml code block
        match = re.search(r"```yaml\s+(.*?)```", body, re.DOTALL)
        if not match:
            raise ValueError("No valid YAML block found")
        yaml_str = match.group(1).strip()
        return yaml.safe_load(yaml_str)
    except Exception as e:
        print("YAML parsing error:", e)
        return None

def main():
    issues = get_open_issues()
    for issue in issues:
        print(f"Issue #{issue['number']}: {issue['title']}")
        # print the issue body too
        print(issue['body'])
        agent_data = parse_yaml_from_issue_body(issue['body'])
        if agent_data:
            print("Parsed agent submission:")
            print(agent_data)
            print()
        else:
            print("No valid agent data found.\n")

if __name__ == "__main__":
    main()
