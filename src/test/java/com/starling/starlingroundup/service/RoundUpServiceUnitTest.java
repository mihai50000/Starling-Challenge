package com.starling.starlingroundup.service;

import com.starling.starlingroundup.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class RoundUpServiceUnitTest {
    private static final ZonedDateTime TIME = ZonedDateTime.parse("2010-11-09T20:41:28.721067Z[UTC]");
    private static final String ACCOUNT_UID = "AUID";
    private static final String FEED_ITEM_UID = "FIUID";
    private static final String CURRENCY = "GBP";
    private static final FeedItem ITEM_1 = FeedItem.builder().feedItemUid(FEED_ITEM_UID).amount(new CurrencyAndAmount(CURRENCY, 108)).build();
    private static final FeedItem ITEM_2 = FeedItem.builder().feedItemUid(FEED_ITEM_UID).amount(new CurrencyAndAmount(CURRENCY, 1000)).build();
    private static final FeedItem ITEM_3 = FeedItem.builder().feedItemUid(FEED_ITEM_UID).amount(new CurrencyAndAmount(CURRENCY, 12413)).build();
    private static final FeedItems FEED_ITEMS = new FeedItems(List.of(ITEM_1, ITEM_2, ITEM_3));

    private static final Account ACCOUNT = Account.builder().accountUid(ACCOUNT_UID).currency(CURRENCY).build();

    private FeedService feedService;

    private RoundUpService roundUpService;

    @BeforeEach
    public void setUp() {
        feedService = Mockito.mock(FeedService.class);
        roundUpService = new RoundUpService(feedService);
    }

    @Test
    public void getRoundUpWindowHappyFlow() {
        Mockito.when(feedService.getSettledFeedItems(ACCOUNT_UID, TIME, TIME.plusWeeks(1))).thenReturn(FEED_ITEMS);
        RoundUpWindow window = roundUpService.getRoundUpWindow(ACCOUNT, TIME, TIME.plusWeeks(1));
        assertEquals(92 + 100 + 87, window.getMinorUnits());
    }

    @Test
    public void getRoundUpWindowInvalidTimeInterval() {
        assertThrows(RuntimeException.class, () -> roundUpService.getRoundUpWindow(ACCOUNT, TIME, TIME));
        assertThrows(RuntimeException.class, () -> roundUpService.getRoundUpWindow(ACCOUNT, TIME, TIME.minusWeeks(1)));
    }
}
