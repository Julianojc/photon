package de.komoot.photon;

import java.util.HashMap;

import de.komoot.photon.nominatim.model.AddressType;
import org.junit.Assert;
import org.junit.Test;

public class PhotonDocTest {

    @Test
    public void testCompleteAddressOverwritesStreet() {
        HashMap<String, String> address = new HashMap<>();
        address.put("street", "test street");
        PhotonDoc doc = createPhotonDocWithAddress(address);
        
        HashMap<String, String> streetNames = new HashMap<>();
        streetNames.put("name", "parent place street");
        doc.setAddressPartIfNew(AddressType.STREET, streetNames);
        
        doc.completeFromAddress();
        AssertUtil.assertAddressName("test street", doc, AddressType.STREET);
    }

    @Test
    public void testCompleteAddressCreatesStreetIfNonExistantBefore() {
        HashMap<String, String> address = new HashMap<>();
        address.put("street", "test street");
        PhotonDoc doc = createPhotonDocWithAddress(address);
        
        doc.completeFromAddress();
        AssertUtil.assertAddressName("test street", doc, AddressType.STREET);
    }

    @Test
    public void testAddCountryCode() {
        PhotonDoc doc = new PhotonDoc(1, "W", 2, "highway", "residential", null, "4", null, null, null, 0, 30, "de", null, 0, 30);

        Assert.assertNotNull(doc.getCountryCode());
        Assert.assertEquals("DE", doc.getCountryCode().getAlpha2());
    }

    private PhotonDoc createPhotonDocWithAddress(HashMap<String, String> address) {
        return new PhotonDoc(1, "W", 2, "highway", "residential", null, "4", address, null, null, 0, 30, null, null, 0, 30);
    }

}
