#include <iostream>
#include <onnxruntime_cxx_api.h>

int main() {
    Ort::Env env(ORT_LOGGING_LEVEL_WARNING, "ONNXApp");
    Ort::SessionOptions session_options;
    session_options.SetIntraOpNumThreads(1);

    Ort::Session session(env, L"model.onnx", session_options);  // 'L' for wide string
    std::cout << "Model loaded successfully!" << std::endl;

    return 0;
}
