# ExpensesBot

ExpensesBot is a Telegram bot that uses Google Sheets as storage to track and manage your expenses. It allows users to:

- View their expenses for the week or day.
- Group expenses by tag for a current month.
- Add new expenses directly via Telegram.

The bot automatically sets up the required spreadsheet structure on first launch — no manual configuration needed.

## How to run

1. Create a Google Cloud service account with Sheets API enabled. [Instruction](https://developers.google.com/sheets/api/quickstart/java)
2. Get a Telegram bot token. [Instruction](https://core.telegram.org/bots/features#botfather)
3. Create a properties file. [Example](./devops/properties/expenses-bot.example.properties)
4. Have Docker installed. [Instruction](https://docs.docker.com/engine/install/ubuntu/)
5. Run:
   ```bash
   docker run -v <YOUR_PROPERTY_FILE>:/config/expenses-bot.properties \
              --name expenses-bot -d sdmitrioul/expenses-bot:1.
   ```

The bot will create and configure the Google Sheet automatically on startup.

## Spreadsheet structure

The spreadsheet is provisioned automatically. Each expense row contains the following columns:

1. **Tags** — category of the expense: *GROCERIES*, *TRANSPORT*, *DOG*, *HOME*, *CAFE*, *BILLS*, *CLOTHES*, *SPORT*, *HEALTH*.
2. **Month** — one of 12 months.
3. **Amount** — numeric value of the expense.
4. **Notes** — short description.
5. **Date** — date of the expense.

## Future plans

- Flexible *Tags* column

## Contribution

You can find more about contribution [here](./documentation/CONTRIBUTION.md)