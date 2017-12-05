package uk.gov.ons.ctp.response.notify.util;

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

public class BitCalculatorTests {

    @Before
    public void setup(){
        this.bitCalculator = new BitCalculator();
    }

    @Test
    public void testValidNotifyKey(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseNotifyKey("rmnotifygatewayfixes1-7c1b5868-f1d9-48af-a12f-b68d5e0905c2-2e58b9a4-d9bc-11e7-9296-cec278b6b50a");
        Assert.assertTrue(info.valid);
        Assert.assertEquals("rmnotifygatewayfixes1", info.keyName);
        Assert.assertEquals(256, info.bits);
    }

    @Test
    public void testInvalidNotifyKey(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseNotifyKey("rmnotifygatewayfixes1-7c1b5868-f1d9-48af-a12f-b68d5e0905c2");

        Assert.assertFalse(info.valid);
        Assert.assertEquals("rmnotifygatewayfixes1", info.keyName);
        Assert.assertEquals(128, info.bits);
    }

    @Test
    public void testDummyKey(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseNotifyKey("dummykey-ffffffff-ffff-ffff-ffff-ffffffffffff-ffffffff-ffff-ffff-ffff-ffffffffffff");

        Assert.assertTrue(info.valid);
        Assert.assertEquals("dummykey", info.keyName);
        Assert.assertEquals(256, info.bits);
    }

    @Test
    public void testBrokenKey(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseNotifyKey("brokenkey-qfffffff-ffff-ffff-ffff-ffffffffffff-ffffffff-ffff-ffff-ffff-ffffffffffff");

        Assert.assertFalse(info.valid);
        Assert.assertEquals("brokenkey", info.keyName);
    }

    @Test
    public void testTemplateId(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseHexNumber("fb7b617a-d5ce-4c0c-812b-e0c3c3a29e2d");

        Assert.assertTrue(info.valid);
        Assert.assertEquals(128, info.bits);
    }

    @Test
    public void testDummyTemplateId(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseHexNumber("ffffffff-ffff-ffff-ffff-ffffffffffff");

        Assert.assertTrue(info.valid);
        Assert.assertEquals(128, info.bits);
    }

    @Test
    public void testBrokenTemplateId(){
        BitCalculator.KeyInfo info  = this.bitCalculator.analyseHexNumber("this-is-not-a-hex-number");

        Assert.assertFalse(info.valid);
    }

    private BitCalculator bitCalculator;
}
