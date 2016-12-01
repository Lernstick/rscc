package ch.imedias.rscc.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.imedias.rscc.model.SupportAddress;

public class SupportAdressTest {

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void testResetAllToDefaults() {
        SupportAddress.resetAllToDefault();
        List<SupportAddress> l = SupportAddress.getAll();
        
        
    }

}
