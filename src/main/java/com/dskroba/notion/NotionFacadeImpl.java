package com.dskroba.notion;

import com.dskroba.base.http.Client;
import com.dskroba.type.Expense;

import java.net.http.HttpRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dskroba.base.Utils.buildUri;

public class NotionFacadeImpl implements NotionFacade {
    private final Client client;
    private final NotionPropertyProvider propertyProvider;
    private final NotionHeadersProvider headersProvider;


    public NotionFacadeImpl(Client client, NotionPropertyProvider propertyProvider, NotionHeadersProvider headersProvider) {
        this.client = client;
        this.propertyProvider = propertyProvider;
        this.headersProvider = headersProvider;
    }

    @Override
    public boolean insertExpense(Expense expense) {
        return Optional.ofNullable(client.loadWebResource(
                buildUri(propertyProvider.getNotionUrl(), "/v1/pages"),
                headersProvider.getPostHeader(),
                "POST",
                HttpRequest.BodyPublishers
                        .ofString(DatabaseUtil.wrapExpense(expense, propertyProvider.getDatabaseId())),
                ignore -> true,
                propertyProvider.getRetryCount(),
                propertyProvider.getRetryDelay()
        )).orElse(false);
    }

    @Override
    public List<Expense> getExpenses(Date from, Date to) {
        return List.of();
    }
}
