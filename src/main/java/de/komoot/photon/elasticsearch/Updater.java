package de.komoot.photon.elasticsearch;

import de.komoot.photon.PhotonDoc;
import de.komoot.photon.Utils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;

/**
 * Updater for elasticsearch
 *
 * @author felix
 */
@Slf4j
public class Updater implements de.komoot.photon.Updater {
    private final Client esClient;
    private BulkRequestBuilder bulkRequest;
    private final String[] languages;

    public Updater(Client esClient, String languages) {
        this.esClient = esClient;
        this.bulkRequest = esClient.prepareBulk();
        this.languages = languages.split(",");
    }

    public void finish() {
        this.updateDocuments();
    }

    @Override
    public void updateOrCreate(PhotonDoc updatedDoc) {
        final boolean exists = this.esClient.get(this.esClient.prepareGet(PhotonIndex.NAME, PhotonIndex.TYPE, String.valueOf(updatedDoc.getPlaceId())).request()).actionGet().isExists();
        if (exists) {
            this.update(updatedDoc);
        } else {
            this.create(updatedDoc);
        }
    }

    public void create(PhotonDoc doc) {
        try {
            this.bulkRequest.add(this.esClient.prepareIndex(PhotonIndex.NAME, PhotonIndex.TYPE).setSource(Utils.convert(doc, this.languages)).setId(String.valueOf(doc.getPlaceId())));
        } catch (IOException e) {
            log.error(String.format("creation of new doc [%s] failed", doc), e);
        }
    }

    public void update(PhotonDoc doc) {
        try {
            this.bulkRequest.add(this.esClient.prepareUpdate(PhotonIndex.NAME, PhotonIndex.TYPE, String.valueOf(doc.getPlaceId())).setDoc(Utils.convert(doc, this.languages)));
        } catch (IOException e) {
            log.error(String.format("update of new doc [%s] failed", doc), e);
        }
    }

    public void delete(Long id) {
        this.bulkRequest.add(this.esClient.prepareDelete(PhotonIndex.NAME, PhotonIndex.TYPE, String.valueOf(id)));
    }

    private void updateDocuments() {
        if (this.bulkRequest.numberOfActions() == 0) {
            log.warn("Update empty");
            return;
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            log.error("error while bulk update: " + bulkResponse.buildFailureMessage());
        }
        this.bulkRequest = this.esClient.prepareBulk();
    }
}
