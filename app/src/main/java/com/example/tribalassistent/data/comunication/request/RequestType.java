package com.example.tribalassistent.data.comunication.request;

public enum RequestType {
    AUTH_LOGIN("Authentication/login"),
    AUTH_COMPLETE_LOGIN("Authentication/completeLogin"),
    AUTH_SELECT_CHARACTER("Authentication/selectCharacter"),
    SYSTEM_IDENTIFY("System/identify"),
    GET_GAME_DATA("GameDataBatch/getGameData"),
    GET_VILLAGE_DATA("VillageBatch/getVillageData"),
    BUILDING_UPGRADE("Building/upgrade"),
    CHARACTER_GET_INFO("Character/getInfo"),
    AUTH_RECONNECT("Authentication/reconnect"),
    BUILDING_COMPLETE("Building/completeInstantly"),
    SECOND_VILLAGE_GET_INFO("SecondVillage/getInfo"),
    SECOND_VILLAGE_START_JOB("SecondVillage/startJob");


    private final String type;

    RequestType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static RequestType fromString(String type) {
        for (RequestType b : RequestType.values()) {
            if (b.getType().equalsIgnoreCase(type)) {
                return b;
            }
        }
        return null;
    }
}
