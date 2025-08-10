package com.dskroba.notion;

import com.dskroba.base.http.Client;
import com.dskroba.base.type.Pair;
import com.dskroba.configurations.properties.NotionProperties;
import com.dskroba.type.Expense;

import java.net.http.HttpRequest;
import java.time.Instant;
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
    public List<Expense> getExpenses(Pair<Instant, Instant> timeInterval) {
        return Optional.ofNullable(client.loadWebResource(
                buildUri(propertyProvider.api().url(),
                        "/v1/databases/" + propertyProvider.databaseId() + "/query"),
                headersProvider.getPostHeader(),
                "POST",
                HttpRequest.BodyPublishers
                        .ofString(DatabaseUtil.wrapTimeInterval(timeInterval)),
                DatabaseUtil::parseExpenses,
                propertyProvider.api().retryCount(),
                propertyProvider.api().retryDelay().toMillis()
        )).orElse(new ArrayList<>());
    }
}
