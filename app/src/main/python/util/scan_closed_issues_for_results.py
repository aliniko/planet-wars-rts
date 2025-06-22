from pathlib import Path

from github import Github
import yaml
import re
import os
from urllib.parse import urlparse



def load_github_token() -> str:
    token_file = Path.home() / ".github_submission_token"
    if not token_file.exists():
        raise FileNotFoundError(f"GitHub token file not found at {token_file}")

    token = token_file.read_text().strip()
    return token

GITHUB_TOKEN = load_github_token()
if not GITHUB_TOKEN:
    raise RuntimeError("GITHUB_TOKEN not set in environment")

REPO_NAME = "SimonLucas/planet-wars-rts-submissions"

def extract_yaml_and_results(issue_body):
    # Extract YAML
    print("Extracting YAML and results from issue body...")
    print(issue_body[:10000])  # Print first 1000 characters for debugging

    yaml_match = re.search(r"```yaml\n(.*?)\n```", issue_body, re.DOTALL)
    yaml_data = yaml.safe_load(yaml_match.group(1)) if yaml_match else {}

    # Extract results block and average
    # result_block_match = re.search(r"📊 Results:\n\n(.+?AVG=([0-9.]+))", issue_body, re.DOTALL)
    result_block_match = re.search(r"📊 Results:\s*\n(.*?AVG\s*=\s*([0-9.]+))", issue_body, re.DOTALL)

    full_results = result_block_match.group(1).strip() if result_block_match else ""
    avg_score = float(result_block_match.group(2)) if result_block_match else None

    print("Extracted YAML:", yaml_data)
    print("Extracted average score:", avg_score)
    print("Extracted full results:", full_results[:1000])  # Print first 1000 characters for debugging

    return yaml_data, avg_score, full_results

def parse_commit_from_url(url):
    # Match /commit/<hash> at the end of the URL
    match = re.search(r"/commit/([a-f0-9]{7,40})$", url)
    return match.group(1) if match else "HEAD"

def extract_entry_id(yaml_data, fallback_url):
    if "id" in yaml_data:
        return yaml_data["id"]
    else:
        return urlparse(fallback_url).path.strip("/").split("/")[-1]

def generate_league_table():
    g = Github(GITHUB_TOKEN)
    repo = g.get_repo(REPO_NAME)
    closed_issues = repo.get_issues(state="closed")

    league_data = []
    full_results_text = ""


    for issue in closed_issues:


        yaml_data, avg_score, full_results = extract_yaml_and_results(issue.body)

        repo_url = yaml_data.get("repo_url")
        if not repo_url or avg_score is None:
            continue

        commit_hash = parse_commit_from_url(repo_url)
        entry_id = extract_entry_id(yaml_data, repo_url)

        league_data.append((entry_id, commit_hash, avg_score))
        full_results_text += f"\n### {entry_id} (`{commit_hash}`)\n\n```\n{full_results}\n```\n"

    league_data.sort(key=lambda x: x[2], reverse=True)

    league_table = "| Entry | Commit | Average Score |\n|---|---|---|\n"
    for entry_id, commit_hash, score in league_data:
        league_table += f"| {entry_id} | `{commit_hash}` | {score:.1f} |\n"

    full_doc = f"# 🏆 PlanetWars League Table\n\n{league_table}\n\n---\n\n## 📋 Full Results\n{full_results_text}"
    return full_doc

if __name__ == "__main__":
    doc = generate_league_table()
    with open("league_table.md", "w") as f:
        f.write(doc)
    print("✅ League table written to league_table.md")
