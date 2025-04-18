package org.acme.examples.aiservices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ModelName;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.openai.testing.internal.OpenAiBaseTest;
import io.quarkiverse.langchain4j.testing.internal.WiremockAware;
import io.quarkus.test.QuarkusUnitTest;

public class MultipleChatModelsDeclarativeServiceTest extends OpenAiBaseTest {

    public static final String MESSAGE_CONTENT = "Tell me a joke about developers";

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(
                    () -> ShrinkWrap.create(JavaArchive.class))
            .overrideRuntimeConfigKey("quarkus.langchain4j.openai.api-key", "defaultKey")
            .overrideRuntimeConfigKey("quarkus.langchain4j.openai.base-url",
                    WiremockAware.wiremockUrlForConfig("/v1"))
            .overrideRuntimeConfigKey("quarkus.langchain4j.openai.model1.api-key", "key1")
            .overrideRuntimeConfigKey("quarkus.langchain4j.openai.model1.base-url",
                    WiremockAware.wiremockUrlForConfig("/v1"))
            .overrideRuntimeConfigKey("quarkus.langchain4j.openai.model2.api-key", "key2")
            .overrideRuntimeConfigKey("quarkus.langchain4j.openai.model2.base-url",
                    WiremockAware.wiremockUrlForConfig("/v1"));

    @BeforeEach
    void setup() {
        setChatCompletionMessageContent(MESSAGE_CONTENT);
        resetRequests();
    }

    @RegisterAiService
    interface ChatWithDefaultModel {

        String chat(String userMessage);
    }

    @RegisterAiService(modelName = "model1")
    interface ChatWithModel1 {

        String chat(String userMessage);
    }

    @RegisterAiService(modelName = "model2")
    interface ChatWithModel2 {

        String chat(String userMessage);
    }

    @RegisterAiService(chatMemoryProviderSupplier = RegisterAiService.NoChatMemoryProviderSupplier.class)
    interface ChatWithRuntimeSelection {

        String chat(@UserMessage String userMessage, @ModelName String model);
    }

    @Inject
    ChatWithDefaultModel chatWithDefaultModel;

    @Inject
    ChatWithModel1 chatWithModel1;

    @Inject
    ChatWithModel2 chatWithModel2;

    @Inject
    ChatWithRuntimeSelection chatWithRuntimeSelection;

    @Test
    @ActivateRequestContext
    public void testDefaultModel() throws IOException {
        String result = chatWithDefaultModel.chat(MESSAGE_CONTENT);
        assertThat(result).isNotBlank();

        assertSingleRequestMessage(getRequestAsMap(), MESSAGE_CONTENT);
    }

    @Test
    @ActivateRequestContext
    public void testNamedModel1() throws IOException {
        String result = chatWithModel1.chat(MESSAGE_CONTENT);
        assertThat(result).isNotBlank();

        assertSingleRequestMessage(getRequestAsMap(), MESSAGE_CONTENT);
    }

    @Test
    @ActivateRequestContext
    public void testNamedModel2() throws IOException {
        String result = chatWithModel2.chat(MESSAGE_CONTENT);
        assertThat(result).isNotBlank();

        assertSingleRequestMessage(getRequestAsMap(), MESSAGE_CONTENT);
    }

    @Test
    @ActivateRequestContext
    public void testRuntimeSelection() throws IOException {
        String result = chatWithRuntimeSelection.chat(MESSAGE_CONTENT, null);
        assertThat(result).isNotBlank();
        assertSingleRequestMessage(getRequestAsMap(), MESSAGE_CONTENT);
        assertThat(singleLoggedRequest().header("Authorization").firstValue()).isEqualTo("Bearer defaultKey");

        setup();

        result = chatWithRuntimeSelection.chat(MESSAGE_CONTENT, "model1");
        assertThat(result).isNotBlank();
        assertSingleRequestMessage(getRequestAsMap(), MESSAGE_CONTENT);
        assertThat(singleLoggedRequest().header("Authorization").firstValue()).isEqualTo("Bearer key1");

        setup();

        result = chatWithRuntimeSelection.chat(MESSAGE_CONTENT, "model2");
        assertThat(result).isNotBlank();
        assertSingleRequestMessage(getRequestAsMap(), MESSAGE_CONTENT);
        assertThat(singleLoggedRequest().header("Authorization").firstValue()).isEqualTo("Bearer key2");

        setup();

        assertThatThrownBy(() -> {
            chatWithRuntimeSelection.chat(MESSAGE_CONTENT, "dummy");
        });
        assertThat(wiremock().getServeEvents()).hasSize(0);
    }

}
