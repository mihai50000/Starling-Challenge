package com.starling.starlingroundup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class FeedServiceUnitTest {
    private static final String ACCOUNT_UID = "AUID";
    private static final ZonedDateTime time = ZonedDateTime.parse("2010-11-09T20:41:28.721067Z[UTC]");

    private FeedService feedService;

    @BeforeEach
    public void setUp() {
        feedService = new FeedService();
    }

    @Test
    public void testGetSettledFeedItemsWrongTimeWindow() {
        assertThrows(RuntimeException.class, ()-> feedService.getSettledFeedItems(ACCOUNT_UID, time, time));
        assertThrows(RuntimeException.class, ()-> feedService.getSettledFeedItems(ACCOUNT_UID, time, time.minusMinutes(1)));
    }
}
