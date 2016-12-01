package ch.imedias.rscc.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ch.imedias.rscc.model.*;
public class SettingsTest {

    @Test
    public void testInitialization() {
        assertTrue(Settings.getQuality() > 0);
        assertTrue(Settings.getCompressionLevel() > 0);
    }

}
