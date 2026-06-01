# SpecGuard
### Enterprise Requirement Gap Analyzer

click here for link : https://spec-guard.onrender.com/

> **4D Code Olympics Submission**
> D1 — Short-Name Ninja · D2 — Enterprise Creator · D3 — Text Processing · D4 — Java

---

## 4D Constraint Compliance

| Dimension | Constraint | Requirement | Status |
|-----------|------------|-------------|--------|
| D1 | Short-Name Ninja | All variable names ≤ 3 characters | ✅ Enforced across all 17 files |
| D2 | Enterprise Creator | Maximum 650 lines of Java | ✅ 548 production lines (563 with tests) |
| D3 | Text Processing | Editors, analyzers, formatters, search tools | ✅ Requirement gap analyzer |
| D4 | Java | Written in Java | ✅ Spring Boot, zero non-Java logic |

---

## What SpecGuard Does

SpecGuard analyzes product specifications and API requirement documents to detect
missing security, operational, and architectural controls.

You paste an unstructured spec. SpecGuard tells you:

- What controls were found in the spec
- What critical controls are missing, ranked by severity
- Which compliance frameworks are violated (PCI-DSS, OWASP, GDPR, ISO 27001, HIPAA)
- What cross-domain security conflicts exist
- A risk score and completeness percentage
- Vague terms that need clarification
- Confidence level of the analysis

The engine catches typos, synonyms, and implicit dependencies that a naive
keyword scanner would miss entirely.

---

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven or Gradle (Gradle wrapper included)

### Run the application

```bash
# Clone or unzip the project
cd olympics

# Run with Gradle wrapper (no installation needed)
.\gradlew.bat bootRun          # Windows
./gradlew bootRun              # Linux / macOS

# Application starts on http://localhost:8080
```

### Run tests

```bash
.\gradlew.bat test             # Windows
./gradlew test                 # Linux / macOS
```

### Build a JAR

```bash
.\gradlew.bat build
java -jar build/libs/specguard-0.0.1-SNAPSHOT.jar
```

---

## Using the API

### Analyze a specification

**Endpoint:** `POST /api/analyze`

**Request:**
```json
{
  "txt": "Your specification text here"
}
```

**Response:**
```json
{
  "rsk": 72,
  "cmp": 20,
  "lbl": "CRITICAL",
  "mis": ["MFA", "ENCRYPT", "RATE_LIMIT", "SESSION", "LOCKOUT"],
  "fnd": ["AUTH", "AUDIT"],
  "amb": ["handle", "process"],
  "vfw": ["PCI_DSS", "OWASP_TOP10"],
  "cnf": 35,
  "trs": {
    "CRITICAL": ["MFA", "ENCRYPT"],
    "HIGH":     ["RATE_LIMIT", "SESSION"],
    "LOW":      ["VERSIONING", "DOCS"]
  },
  "den": {
    "AUTH": 2,
    "AUDIT": 1
  },
  "wrn": null
}
```

**Response fields explained:**

| Field | Type | Description |
|-------|------|-------------|
| `rsk` | int | Risk score 0–100. Higher = more dangerous gaps |
| `cmp` | int | Completeness percentage. How much of the required spec is covered |
| `lbl` | string | CRITICAL / MODERATE / LOW |
| `mis` | array | Missing controls, ordered by severity (most critical first) |
| `fnd` | array | Controls detected in the spec |
| `amb` | array | Vague terms found (handle, process, manage, etc.) |
| `vfw` | array | Compliance frameworks violated based on missing controls |
| `cnf` | int | Confidence 0–100. Low confidence = spec too vague or short |
| `trs` | object | Missing controls grouped into CRITICAL / HIGH / LOW tiers |
| `den` | object | Keyword density — how many times each control was mentioned |
| `wrn` | string | Warning message if spec is too short, empty, or too vague |

### Health check

```bash
GET /api/health
# Returns: "OK"
```

### Example curl commands

```bash
# Basic analysis
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"txt": "Build a payment API. Authenticate users with JWT. Log all transactions."}'

# Short spec (triggers quality gate warning)
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"txt": "build something"}'

# Spec with typos (Levenshtein fuzzy matching)
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"txt": "Authetication via JWT. Encrypion required. Audiit all transactions."}'
```

---

## Engine Architecture

The engine is a 5-stage pipeline. There is **zero if-else logic** in the analysis.
All domain rules live as data in two classes (`Tri.java` and `Grp.java`).
Adding a new domain requires one line of data. The algorithm never changes.

```
Raw text input
      │
      ▼
 [Stage 1] Tok.java — Tokenizer
  LinkedList<String> token stream
  Lowercases, strips punctuation
      │
      ▼
 [Stage 2] Syn.java + Tri.java — Synonym resolution + Trie matching
  HashMap synonym graph resolves variants ("verify" → "auth")
  Trie performs O(L) keyword matching per token
  Fzy.java fuzzy fallback catches typos via Levenshtein DP table
      │
      ▼
 [Stage 3] Grp.java + Bfs.java — Requirement graph + BFS expansion
  HashMap<String, Set<String>> directed graph of domain dependencies
  BFS discovers all transitive requirements from detected domains
  Cfl.java detects cross-domain conflicts (LOGIN + PAYMENT → SESSION_FIXATION)
      │
      ▼
 [Stage 4] Cov.java — HashSet coverage analysis
  mis = required − found via HashSet.removeAll()
  O(1) per lookup, no loop scanning
      │
      ▼
 [Stage 5] Rnk.java — PriorityQueue severity ranking
  PriorityQueue<Req> orders missing controls by severity score
  Risk score computed from weighted missing control set
      │
      ▼
 JSON response
```

---

## Data Structures Used

Every data structure below has a specific algorithmic reason. None are decorative.

| Class | Data Structure | Purpose | Complexity |
|-------|---------------|---------|------------|
| `Tri.java` | Trie (HashMap tree) | Keyword vocabulary matching | O(L) per token |
| `Grp.java` | HashMap&lt;String, Set&lt;String&gt;&gt; | Requirement dependency graph | O(1) lookup |
| `Grp.java` | HashMap&lt;String, Set&lt;String&gt;&gt; | Compliance framework map | O(1) lookup |
| `Bfs.java` | Queue + HashSet | Transitive requirement discovery | O(V+E) |
| `Cov.java` | HashSet subtraction | Gap detection | O(1) per check |
| `Rnk.java` | PriorityQueue&lt;Req&gt; | Severity-ranked output | O(n log n) |
| `Tok.java` | LinkedList&lt;String&gt; | Token stream processing | O(n) |
| `Cfl.java` | HashMap&lt;String, Set&lt;String&gt;&gt; | Cross-domain conflict rules | O(1) lookup |
| `Syn.java` | HashMap&lt;String, String&gt; | Synonym resolution graph | O(1) per token |
| `Idx.java` | HashMap&lt;String, List&lt;Integer&gt;&gt; | Inverted index (keyword density) | O(1) amortized |
| `Lru.java` | LinkedHashMap (access-order) | LRU analysis cache | O(1) get/put |
| `Fzy.java` | int[][] DP table | Levenshtein edit distance | O(m×n) per pair |

**Total: 12 distinct data structures across 17 Java files.**

---

## Features

### Core pipeline
- Trie-based vocabulary matching at O(L) per token
- BFS graph expansion discovers implicit transitive requirements
- HashSet subtraction for O(1) gap detection

### Synonym expansion
Resolves vocabulary variants before Trie lookup. "verify", "credential",
"signin", "identity" all correctly map to the AUTH domain. No hardcoded aliases
— synonyms live in a HashMap data structure.

### Fuzzy typo matching
Levenshtein distance computed via dynamic programming (int[][] table).
Catches typos up to edit distance 2. "authetication", "encrypion", "audiit"
are all correctly identified despite misspelling.

### Compliance framework tagging
Detects which industry frameworks are violated based on missing controls:
- PCI-DSS (payment card security)
- OWASP Top 10 (web application security)
- GDPR (data protection)
- ISO 27001 (information security management)
- HIPAA (healthcare data)

### Cross-domain conflict detection
When a spec contains multiple domains simultaneously, the engine detects
dangerous combinations requiring additional controls. LOGIN + PAYMENT together
triggers SESSION_FIXATION and CSRF. Rules are encoded as a HashMap — pure data.

### Severity tier grouping
Missing controls grouped into CRITICAL (severity ≥ 8), HIGH (≥ 5), LOW (< 5)
using existing PriorityQueue severity scores. No additional ranking logic.

### LRU analysis cache
Identical specs skip the full pipeline and return from cache in O(1).
Uses access-ordered LinkedHashMap with removeEldestEntry() — the canonical
Java LRU pattern.

### Inverted index density
Tracks how many times each control keyword appears in the spec. A spec
mentioning AUTH once is flagged differently from one mentioning it five times.

### Dynamic confidence scoring
Confidence = matched tokens / total tokens × 100. Short or vague specs
produce low confidence scores, warning the user before they act on results.

### Quality gate
Warns when the spec is too short (< 15 tokens), has no detectable domain,
or when ambiguous terms outnumber specific ones.

---

## Project Structure

```
src/main/java/com/specguard/
├── App.java       Spring Boot entry point                    (9 lines)
├── Ctl.java       REST controller — /api/analyze endpoint    (23 lines)
├── Eng.java       Engine orchestrator — runs the pipeline    (112 lines)
├── Tok.java       Tokenizer — LinkedList token stream        (15 lines)
├── Tri.java       Trie — O(L) keyword matching               (68 lines)
├── Syn.java       Synonym graph — vocabulary expansion       (33 lines)
├── Fzy.java       Fuzzy matcher — Levenshtein DP table       (32 lines)
├── Grp.java       Requirement graph + compliance frameworks  (71 lines)
├── Bfs.java       BFS expander — transitive requirements     (22 lines)
├── Cfl.java       Conflict detector — cross-domain rules     (32 lines)
├── Cov.java       Coverage analyzer — HashSet gap detection  (18 lines)
├── Rnk.java       Risk ranker — PriorityQueue severity sort  (26 lines)
├── Idx.java       Inverted index — keyword density           (24 lines)
├── Lru.java       LRU cache — access-order LinkedHashMap     (20 lines)
├── Req.java       Requirement model (Comparable)             (17 lines)
└── Res.java       Analysis result model                      (26 lines)

src/test/java/com/specguard/
└── AppTest.java   JUnit Integration Test                     (15 lines)

src/main/resources/static/
└── index.html     Frontend UI (Single-file HTML with responsive Tailwind CSS & custom JS)

Total production Java: 548 lines
Total with tests:      563 lines
Budget used:           563 / 650 (87 lines remaining)
```

---

## D1 Constraint — Variable Name Reference

All identifiers in the codebase are ≤ 3 characters. Key mappings:

| Concept | Identifier | Class |
|---------|-----------|-------|
| engine | `eng` | `Eng` |
| result | `res` | `Res` |
| requirement | `req` | `Req` |
| trie | `tri` | `Tri` |
| graph | `grp` | `Grp` |
| tokenizer | `tok` | `Tok` |
| BFS | `bfs` | `Bfs` |
| coverage | `cov` | `Cov` |
| ranker | `rnk` | `Rnk` |
| conflict | `cfl` | `Cfl` |
| synonym | `syn` | `Syn` |
| fuzzy | `fzy` | `Fzy` |
| index | `idx` | `Idx` |
| LRU cache | `lru` | `Lru` |
| found | `fnd` | — |
| missing | `mis` | — |
| required | `req` | — |
| domain | `dom` | — |
| tokens | `tks` | — |
| risk | `rsk` | — |
| completeness | `cmp` | — |
| confidence | `cnf` | — |
| severity | `sev` | — |
| priority queue | `pq` | — |
| queue (BFS) | `que` | — |
| visited | `vis` | — |

---

## Example Analysis

**Input spec:**
```
Build a payment API endpoint. Users must authenticate via JWT.
Accept JSON card details. Store transaction records in the database.
Handle errors gracefully.
```

**Output:**
```json
{
  "rsk": 72,
  "cmp": 12,
  "lbl": "CRITICAL",
  "mis": ["ENCRYPT", "MFA", "AUDIT", "RATE_LIMIT", "SESSION", "TOKEN_EXPIRY",
          "IDEMPOTENCY", "LOCKOUT", "BACKUP", "REFUND", "VALIDATION"],
  "fnd": ["AUTH", "PAYMENT"],
  "amb": ["handle"],
  "vfw": ["PCI_DSS", "OWASP_TOP10", "GDPR"],
  "cnf": 23,
  "trs": {
    "CRITICAL": ["ENCRYPT", "MFA", "AUDIT"],
    "HIGH":     ["RATE_LIMIT", "SESSION", "TOKEN_EXPIRY", "IDEMPOTENCY", "LOCKOUT"],
    "LOW":      ["BACKUP", "REFUND", "VALIDATION"]
  },
  "den": { "AUTH": 1, "PAYMENT": 1 },
  "wrn": null
}
```

**Typo test — every keyword is misspelled:**
```
Authetication via JWT. Encrypion required. Audiit all transactions. Payement processing.
```

**Output:** AUTH, ENCRYPT, AUDIT, PAYMENT all correctly detected via Levenshtein DP.

---

## Supported Domains and Controls

### Domains detected (trigger BFS expansion)
`PAYMENT` `LOGIN` `AUTH` `API` `DATA` `MONITOR`

### Controls checked
`AUTH` `ENCRYPT` `AUDIT` `MFA` `SESSION` `LOCKOUT` `RATE_LIMIT`
`TOKEN_EXPIRY` `IDEMPOTENCY` `REFUND` `BACKUP` `VALIDATION`
`VERSIONING` `DOCS` `CAPTCHA` `ALERT` `DASHBOARD` `LOG_RETENTION`
`SESSION_FIXATION` `CSRF` `WEBHOOK_AUTH` `TOKEN_ROTATION` `SCOPE_LIMIT`
`FRAUD_DETECTION` `PCI_SCOPE_ISOLATION`

### Compliance frameworks
`PCI_DSS` `OWASP_TOP10` `GDPR` `ISO_27001` `HIPAA`

### Synonym vocabulary (sample)
`authenticate` `verify` `credential` `signin` → AUTH
`secure` `cipher` `encoded` `hashed` → ENCRYPT
`track` `record` `history` `journal` → AUDIT
`billing` `invoice` `checkout` `purchase` → PAYMENT

---

## Why No If-Else

The old approach encoded rules as code:
```java
if (spec.contains("payment")) {
    missing.add("AUTH");
    missing.add("ENCRYPT");
}
```

Adding a new domain meant writing new code. The algorithm and the rules
were the same thing.

SpecGuard separates them. The algorithm (BFS, Trie traversal, HashSet
subtraction) is fixed. The rules are data:

```java
// Grp.java — adding a domain is one line
g.add("PAYMENT", "AUTH", "ENCRYPT", "AUDIT", "REFUND", "IDEMPOTENCY");

// Tri.java — adding a keyword is one line  
t.add("stripe", "PAYMENT");
```

A new compliance framework is five words:
```java
g.fwk("SOC2", "AUDIT", "BACKUP", "MFA", "RATE_LIMIT");
```

The engine never changes. The data grows.

---

*SpecGuard · Code Olympics 4D · Text Processing in Java*
*D1 Short-Name Ninja · D2 Enterprise Creator · D3 Text Processing · D4 Java*
