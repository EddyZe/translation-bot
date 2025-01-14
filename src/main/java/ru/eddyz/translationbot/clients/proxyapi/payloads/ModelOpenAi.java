package ru.eddyz.translationbot.clients.proxyapi.payloads;




public enum ModelOpenAi {
    GPT_4_TURBO("gpt-4-turbo"), GPT_4o_MINI("gpt-4o-mini"), GPT_3_5_TURBO("gpt-3.5-turbo"),
    O1_MINI("o1-mini"), O1("o1"), GPT_4_O("gpt-4-o");
    private final String model;

    ModelOpenAi(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return model;
    }
}
