package com.example.tribalassistent.data.repositories;

import com.example.tribalassistent.Service.building.Manager;
import com.example.tribalassistent.data.comunication.EventType;
import com.example.tribalassistent.data.comunication.MessagerSync;
import com.example.tribalassistent.data.comunication.Observer;
import com.example.tribalassistent.data.comunication.Result;
import com.example.tribalassistent.data.comunication.Subject;
import com.example.tribalassistent.data.model.common.Resources;
import com.example.tribalassistent.data.model.village.Building;
import com.example.tribalassistent.data.model.village.ProductionRates;
import com.example.tribalassistent.data.model.village.Village;
import com.example.tribalassistent.data.model.village.VillageData;

import java.util.Date;
import java.util.Map;

import lombok.Setter;

@Setter
public class VillageGameBatch implements Observer {
    private static VillageGameBatch instance;
    private Map<Integer, VillageData> villageData;


    private VillageGameBatch() {
        villageData = requestVillageData();
        MessagerSync.getInstance().registerObserver(this);
    }

    public static VillageGameBatch getInstance() {
        if (instance == null) {
            instance = new VillageGameBatch();
        }
        return instance;
    }

    public VillageData getVillageData(int villageId) {
        return villageData.get(villageId);
    }

    public Building getBuilding(String building_name, int villageId) {
        return getVillageData(villageId).getVillage().getBuildings().get(building_name);
    }

    public Resources getResources(int villageId) {
        Village village = getVillageData(villageId).getVillage();
        Resources resources = village.getResources();
        ProductionRates rates = village.getProduction_rates();

        int lastUpdate = village.getRes_last_update();
        int now = (int) (new Date().getTime() / 1000);
        float hourDifference = (float) (now - lastUpdate) / 3600;

        Resources result = new Resources();
        result.setClay(currentAmount(resources.getClay(), +rates.getClay(), hourDifference));
        result.setFood(currentAmount(resources.getFood(), +rates.getFood(), hourDifference));
        result.setIron(currentAmount(resources.getIron(), +rates.getIron(), hourDifference));
        result.setWood(currentAmount(resources.getWood(), +rates.getWood(), hourDifference));

        return result;
    }


    private int currentAmount(int current, Double rate, float timeInterval) {
        return (int) (current + rate * timeInterval);
    }

    private Map<Integer, VillageData> requestVillageData() {
        Result result = MessagerSync.send(CharacterRepository.getInstance().getVillageIds(), EventType.GET_VILLAGE_DATA);
        if (result instanceof Result.Error) {
            System.out.println(((Result.Error) result).getError());
            return null;
        }
        return ((Result.Success<Map<Integer, VillageData>>) result).getData();
    }


    @Override
    public void update(Subject observable) {
        resourceChanged((Village) observable.getEvent(EventType.VILLAGE_RESOURCE_CHANGED));
    }

    private void resourceChanged(Village remote) {
        if (remote != null) {
            Village local = getVillageData(remote.getVillageId()).getVillage();
            local.setBase_storage(remote.getBase_storage());
            local.setBuilding_queue_slots(remote.getBuilding_queue_slots());
            local.setLoyalty(remote.getLoyalty());
            local.setProduction_rates(remote.getProduction_rates());
            local.setRes_last_update(remote.getRes_last_update());
            local.setResources(remote.getResources());
            local.setStorage(remote.getStorage());
            Manager.getInstance().run(remote.getVillageId());
        }
    }
}
