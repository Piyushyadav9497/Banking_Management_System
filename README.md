# Banking Management System (Java)

A console-based Banking Management System built for a Java internship project.
Demonstrates: OOP design, JDBC database connectivity, input validation, and
custom exception handling.

## Project Structure

```
BankingManagementSystem/
├── pom.xml                      # Maven build file (MySQL driver dependency)
├── sql/schema.sql                # Run this in MySQL first
├── README.md
└── src/main/java/com/bank/
    ├── Main.java                  # Console menu (entry point)
    ├── model/                     # Customer, Account, Transaction + enums
    ├── exception/                  # Custom checked exceptions
    ├── db/DBConnection.java        # JDBC connection manager
    ├── dao/                        # Database access (SQL) layer
    ├── service/BankingService.java # Business logic + validation glue
    └── util/Validator.java         # Reusable input validation
```

### Why this structure (OOP concepts used)
- **Encapsulation**: model classes keep fields `private` with getters/setters;
  money is stored as `BigDecimal`, never `double`, to avoid rounding errors.
- **Enums over free text**: `AccountType` (SAVINGS/CURRENT), `AccountStatus`
  (ACTIVE/CLOSED), and `TransactionType` prevent invalid values from ever
  being stored.
- **Layering**: UI (Main) → Service (business rules) → DAO (SQL) → DB.
  Main never touches SQL directly.
- **Custom exception hierarchy**: `BankException` is the base class;
  `CustomerNotFoundException`, `AccountNotFoundException`,
  `InsufficientBalanceException`, `AccountClosedException`,
  `InvalidInputException`, and `DuplicateEntryException` extend it, so
  callers can catch broadly or specifically.
- **Single Responsibility**: `Validator` only validates, DAOs only talk to
  the DB, `BankingService` only enforces business rules (e.g. you can't
  withdraw more than the balance, or transact on a closed account).

## Prerequisites

- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- MySQL Server 8.x running locally

## Setup

### 1. Create the database
```bash
mysql -u root -p < sql/schema.sql
```
This creates `bank_db` with `customers`, `accounts`, and `transactions`
tables, plus one sample customer and account.

### 2. Configure DB credentials
`DBConnection.java` reads credentials from environment variables (with
fallback defaults `root` / `password`):

```bash
export BANK_DB_URL="jdbc:mysql://localhost:3306/bank_db"
export BANK_DB_USER="root"
export BANK_DB_PASSWORD="your_password_here"
```
(On Windows PowerShell: `$env:BANK_DB_PASSWORD="your_password_here"`)

Alternatively, edit the default values directly in
`src/main/java/com/bank/db/DBConnection.java`.

### 3. Build the project
```bash
mvn clean package
```
This downloads the MySQL connector and produces a runnable fat-jar at:
`target/banking-management-system-jar-with-dependencies.jar`

### 4. Run
```bash
java -jar target/banking-management-system-jar-with-dependencies.jar
```

You'll see a numbered menu to add customers, open accounts, deposit,
withdraw, transfer between accounts, view statements, close accounts, and
view the full transaction ledger.

## Business rules enforced

- A deposit/withdrawal/transfer amount must be a positive number with at
  most 2 decimal places.
- A withdrawal or transfer that would overdraw the account is rejected
  with `InsufficientBalanceException`.
- No transaction is allowed on a `CLOSED` account
  (`AccountClosedException`).
- An account can only be closed when its balance is exactly zero.
- A transfer is recorded as two linked ledger rows (`TRANSFER_OUT` on the
  sender, `TRANSFER_IN` on the receiver) so each account's statement is
  self-contained and auditable.

## Testing the app (manual test plan for your submission)

1. **Add Customer** — add a customer with a valid email/phone → confirm
   success; try a bad email (`notanemail`) → confirm `InvalidInputException`.
2. **Duplicate check** — add a second customer with the same email →
   confirm `DuplicateEntryException`.
3. **Open Account** — open a SAVINGS account with an initial deposit of
   1000 → confirm the generated account number and balance.
4. **Invalid account type** — try opening an account with type `FIXED` →
   confirm a clear validation error.
5. **Deposit** — deposit 500 into the account → confirm balance increases
   and a `DEPOSIT` row appears in the statement.
6. **Withdraw** — withdraw more than the balance → confirm
   `InsufficientBalanceException`; then withdraw a valid amount → confirm
   success.
7. **Transfer** — open a second account and transfer funds between the two
   → confirm both balances update and both accounts show a linked
   transaction in their statements.
8. **Closed account** — close an account with a zero balance, then try to
   deposit into it → confirm `AccountClosedException`.
9. **Non-existent IDs** — try operating on an account number or customer ID
   that doesn't exist → confirm `AccountNotFoundException` /
   `CustomerNotFoundException`.
10. **Statement & ledger** — view a single account's statement and the
    full transaction list across all accounts.

Document the results of each test (screenshots or console output) for your
internship submission, along with a short write-up of the workflow above.

## Suggested submission checklist

- [ ] Source code (this project) pushed to GitHub / zipped
- [ ] `schema.sql` included
- [ ] README (this file) explaining setup & run steps
- [ ] Screenshots or logs of the manual test plan above
- [ ] Short write-up: what OOP concepts, validation, and exception handling
      were used and why (you can largely reuse the "Why this structure"
      section above in your own words)

## Possible extensions (if you want to go further)

- Add interest calculation for SAVINGS accounts.
- Add a minimum-balance rule for CURRENT accounts.
- Add a simple login/PIN step before allowing transactions.
- Replace the console UI with a Spring Boot + REST API, or a JavaFX UI.
- Add unit tests (JUnit + Mockito) for `BankingService` and `Validator`.
- Add a monthly/periodic statement export to PDF or CSV.
