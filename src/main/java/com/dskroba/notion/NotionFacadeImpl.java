package com.dskroba.notion;

import com.dskroba.base.http.Client;
import com.dskroba.configurations.properties.NotionProperties;
import com.dskroba.expenses.Expense;
import com.dskroba.type.Currency;
import com.dskroba.type.Timeframe;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dskroba.base.Utils.buildUri;

public class NotionFacadeImpl implements NotionFacade {
    private final Client client;
    private final NotionProperties propertyProvider;
    private final NotionHeadersProvider headersProvider;

    NotionFacadeImpl(Client client, NotionProperties notionProperties, NotionHeadersProvider headersProvider) {
        this.client = client;
        this.propertyProvider = notionProperties;
        this.headersProvider = headersProvider;
    }

    @Override
    public boolean insertExpense(Expense expense) {
        return Optional.ofNullable(client.loadWebResource(
                buildUri(propertyProvider.api().url(), "/v1/pages"),
                headersProvider.getPostHeader(),
                "POST",
                HttpRequest.BodyPublishers
                        .ofString(DatabaseUtil.wrapExpense(expense, propertyProvider.databaseId())),
                ignore -> true,
                propertyProvider.api().retryCount(),
                propertyProvider.api().retryDelay().toMillis()
        )).orElse(false);
    }

    @Override
    public List<Expense> getExpenses(Timeframe timeframe, Long groupId, Currency currency) {
        return Optional.ofNullable(client.loadWebResource(
                buildUri(propertyProvider.api().url(),
                        "/v1/databases/" + propertyProvider.databaseId() + "/query"),
                headersProvider.getPostHeader(),
                "POST",
                HttpRequest.BodyPublishers
                        .ofString(DatabaseUtil.wrapTimeInterval(timeframe)),
                DatabaseUtil::parseExpenses,
                propertyProvider.api().retryCount(),
                propertyProvider.api().retryDelay().toMillis()
        )).orElse(new ArrayList<>());
    }
}
