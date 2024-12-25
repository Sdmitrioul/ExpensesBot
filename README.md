# ExpensesBot
docker build -t expense-bot:latest .

docker run -v ./devops/properties/expnses-bot.dev.properties:/config/expenses-bot.properties expense-bot:latest