# ExpensesBot

ExpenseBot is a Telegram bot that integrates with the Notion API to track and manage your expenses. It allows users to:

- View their expenses for the week or day.
- Group expenses by tag for a current month.
- Add new expenses to the system directly via Telegram.

This bot leverages the Notion API to fetch and manage data, and integrates with Telegram to provide a user-friendly interface for interacting with the system.

## How to run
1. Create Notion integration. [Here is instruction](https://developers.notion.com/docs/create-a-notion-integration)
2. Connect it to your database in Notion. [About database structure](#database-structure)
3. Obtain database id. (You can find it in URL of page that contains database: `https://www.notion.so/<DATABASE_ID>?v=<...>)
4. Get telegram bot token. [Instruction](https://core.telegram.org/bots/features#botfather)
5. Create properties file. [Example](./devops/properties/expenses-bot.example.properties)
6. Have installed docker. [Instruction](https://docs.docker.com/engine/install/ubuntu/)
7. Run `docker run -v <YOUR_PROPERTY_FILE>:/config/expenses-bot.properties --name expenses-bot -d  sdmitrioul/expenses-bot:1.1`

## Database structure

Fields of database are presented on image.
![img.png](documentation/database_header.png)

Fields description:
1. Tags - multi-select value, one of: *GROCERIES*, *TRANSPORT*, *DOG*, *HOME*, *CAFE*, *BILLS*, *CLOTHES*, *SPORT*, *HEALTH*.
2. Month - select: one of 12 months.
3. Amount - number value, currency could be added optionally.
4. Notes - title.
5. Date - date type.
6. Time - formula: `if(empty(prop("Date")), prop("Created Time"), prop("Date"))`.

## Future plans

- Graphics of expenses
- Flexible *Tags* column
- Database creator

## Contribution

You can find more about contribution [here](./documentation/CONTRIBUTION.md)
