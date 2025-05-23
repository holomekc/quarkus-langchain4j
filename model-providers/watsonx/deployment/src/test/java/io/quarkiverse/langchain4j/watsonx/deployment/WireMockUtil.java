package io.quarkiverse.langchain4j.watsonx.deployment;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicReference;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

public class WireMockUtil {

    public static final Long DEFAULT_TIME_LIMIT = 10000l;

    public static final int PORT_WATSONX_SERVER = 8089;
    public static final String URL_WATSONX_SERVER = "http://localhost:8089";
    public static final String URL_WATSONX_CHAT_API = "/ml/v1/text/chat?version=%s";
    public static final String URL_WATSONX_CHAT_STREAMING_API = "/ml/v1/text/chat_stream?version=%s";
    public static final String URL_WATSONX_GENERATION_API = "/ml/v1/text/generation?version=%s";
    public static final String URL_WATSONX_GENERATION_STREAMING_API = "/ml/v1/text/generation_stream?version=%s";
    public static final String URL_WATSONX_SCORING_API = "/ml/v1/text/rerank?version=%s";
    public static final String URL_WATSONX_EMBEDDING_API = "/ml/v1/text/embeddings?version=%s";
    public static final String URL_WATSONX_TOKENIZER_API = "/ml/v1/text/tokenization?version=%s";
    public static final String URL_WATSONX_TEXT_EXTRACTION_START_API = "/ml/v1/text/extractions?version=%s";
    public static final String URL_WATSONX_TEXT_EXTRACTION_RESULT_API = "/ml/v1/text/extractions/%s?project_id=%s&version=%s";

    public static final int PORT_IAM_SERVER = 8090;
    public static final String URL_IAM_SERVER = "http://localhost:8090";
    public static final String URL_IAM_GENERATE_TOKEN = "/identity/token";

    public static final int PORT_WX_SERVER = 8091;
    public static final String URL_WX_SERVER = "http://localhost:8091";
    public static final String URL_WX_AGENT_TOOL_RUN = "/v1-beta/utility_agent_tools/run";

    public static final int PORT_COS_SERVER = 8092;
    public static final String URL_COS_SERVER = "http://localhost:8092";

    public static final String API_KEY = "my_super_api_key";
    public static final String BEARER_TOKEN = "my_super_token";
    public static final String PROJECT_ID = "123123321321";
    public static final String GRANT_TYPE = "urn:ibm:params:oauth:grant-type:apikey";
    public static final String VERSION = "2024-03-14";
    public static final String DEFAULT_CHAT_MODEL = "mistralai/mistral-large";
    public static final String DEFAULT_EMBEDDING_MODEL = "ibm/slate-125m-english-rtrvr";
    public static final String DEFAULT_SCORING_MODEL = "cross-encoder/ms-marco-minilm-l-12-v2";
    public static final String IAM_200_RESPONSE = """
            {
                "access_token": "%s",
                "refresh_token": "not_supported",
                "token_type": "Bearer",
                "expires_in": 3600,
                "expiration": %d,
                "scope": "ibm openid"
            }
            """;
    public static String RESPONSE_WATSONX_GENERATION_API = """
            {
                "model_id": "mistralai/mistral-large",
                "created_at": "2024-01-21T17:06:14.052Z",
                "results": [
                    {
                        "generated_text": "AI Response",
                        "generated_token_count": 5,
                        "input_token_count": 50,
                        "stop_reason": "eos_token",
                        "seed": 2123876088
                    }
                ]
            }
            """;
    public static String RESPONSE_WATSONX_CHAT_API = """
            {
                "id": "cmpl-15475d0dea9b4429a55843c77997f8a9",
                "model_id": "mistralai/mistral-large",
                "created": 1689958352,
                "created_at": "2023-07-21T16:52:32.190Z",
                "choices": [{
                    "index": 0,
                    "message": {
                        "role": "assistant",
                        "content": "AI Response"
                    },
                    "finish_reason": "stop"
                }],
                "usage": {
                    "completion_tokens": 47,
                    "prompt_tokens": 59,
                    "total_tokens": 106
                }
            }""";
    public static String RESPONSE_WATSONX_SCORING_API = """
            {
            "model_id": "cross-encoder/ms-marco-minilm-l-12-v2",
            "created_at": "2024-10-18T06:57:42.032Z",
            "results": [
                {
                    "index": 5,
                    "score": 9.720501899719238
                },
                {
                    "index": 1,
                    "score": 8.770895957946777
                },
                {
                    "index": 0,
                    "score": -2.5847978591918945
                },
                {
                    "index": 3,
                    "score": -3.349348306655884
                },
                {
                    "index": 4,
                    "score": -3.920926570892334
                },
                {
                    "index": 2,
                    "score": -4.939967155456543
                }
            ],
            "input_token_count": 318
            }""";
    public static String RESPONSE_WATSONX_EMBEDDING_API = """
            {
                "model_id": "%s",
                "results": [
                  {
                    "embedding": [
                      -0.006929283,
                      -0.005336422,
                      -0.024047505
                    ]
                  }
                ],
                "created_at": "2024-02-21T17:32:28Z",
                "input_token_count": 10
            }
            """;
    public static String RESPONSE_WATSONX_CHAT_STREAMING_API = """
            id: 1
            event: message
            data: {"id":"chat-049e3ff7ff08416fb5c334d05af059da","model_id":"mistralai/mistral-large","choices":[{"index":0,"finish_reason":null,"delta":{"role":"assistant"}}],"created":1728810714,"model_version":"2.0.0","created_at":"2024-10-13T09:11:55.072Z","usage":{"prompt_tokens":88,"total_tokens":88},"system":{"warnings":[{"message":"This model is a Non-IBM Product governed by a third-party license that may impose use restrictions and other obligations. By using this model you agree to its terms as identified in the following URL.","id":"disclaimer_warning","more_info":"https://dataplatform.cloud.ibm.com/docs/content/wsj/analyze-data/fm-models.html?context=wx"},{"message":"The value of 'time_limit' for this model must be larger than 0 and not larger than 10m0s; it was set to 10m0s","id":"time_limit_out_of_range","additional_properties":{"limit":600000,"new_value":600000,"parameter":"time_limit","value":999000}}]}}

            id: 2
            event: message
            data: {"id":"chat-049e3ff7ff08416fb5c334d05af059da","model_id":"mistralai/mistral-large","choices":[{"index":0,"finish_reason":null,"delta":{"content":" He"}}],"created":1728810714,"model_version":"2.0.0","created_at":"2024-10-13T09:11:55.073Z","usage":{"completion_tokens":1,"prompt_tokens":88,"total_tokens":89}}

            id: 3
            event: message
            data: {"id":"chat-049e3ff7ff08416fb5c334d05af059da","model_id":"mistralai/mistral-large","choices":[{"index":0,"finish_reason":"stop","delta":{"content":"llo"}}],"created":1728810714,"model_version":"2.0.0","created_at":"2024-10-13T09:11:55.090Z","usage":{"completion_tokens":2,"prompt_tokens":88,"total_tokens":90}}

            id: 4
            event: message
            data: {"id":"chat-049e3ff7ff08416fb5c334d05af059da","model_id":"mistralai/mistral-large","choices":[],"created":1728810714,"model_version":"2.0.0","created_at":"2024-10-13T09:11:55.715Z","usage":{"completion_tokens":36,"prompt_tokens":88,"total_tokens":124}}

            id: 5
            event: close
            data: {}
            """;
    public static String RESPONSE_WATSONX_GENERATION_STREAMING_API = """
            id: 1
            event: message
            data: {}

            id: 2
            event: message
            data: {"modelId":"mistralai/mistral-large","model_version":"2.1.0","created_at":"2024-05-04T14:29:19.162Z","results":[{"generated_text":"","generated_token_count":0,"input_token_count":2,"stop_reason":"not_finished"}]}

            id: 3
            event: message
            data: {"model_id":"mistralai/mistral-large","model_version":"2.1.0","created_at":"2024-05-04T14:29:19.203Z","results":[{"generated_text":". ","generated_token_count":2,"input_token_count":0,"stop_reason":"not_finished"}]}

            id: 4
            event: message
            data: {"model_id":"mistralai/mistral-large","model_version":"2.1.0","created_at":"2024-05-04T14:29:19.223Z","results":[{"generated_text":"I'","generated_token_count":3,"input_token_count":0,"stop_reason":"not_finished"}]}

            id: 5
            event: message
            data: {"model_id":"mistralai/mistral-large","model_version":"2.1.0","created_at":"2024-05-04T14:29:19.243Z","results":[{"generated_text":"m ","generated_token_count":4,"input_token_count":0,"stop_reason":"not_finished"}]}

            id: 6
            event: message
            data: {"model_id":"mistralai/mistral-large","model_version":"2.1.0","created_at":"2024-05-04T14:29:19.262Z","results":[{"generated_text":"a beginner","generated_token_count":5,"input_token_count":0,"stop_reason":"max_tokens"}]}

            id: 7
            event: close
            data: {}
            """;
    public static final String RESPONSE_WATSONX_TOKENIZER_API = """
              {
              "model_id": "%s",
              "result": {
                "token_count": 11,
                "tokens": [
                  "Write",
                  "a",
                  "tag",
                  "line",
                  "for",
                  "an",
                  "alumni",
                  "associ",
                  "ation:",
                  "Together",
                  "we"
                ]
              }
            }
            """;

    public static StreamingChatResponseHandler streamingChatResponseHandler(AtomicReference<ChatResponse> streamingResponse) {
        return new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                streamingResponse.set(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                fail(error);
            }
        };
    }
}
