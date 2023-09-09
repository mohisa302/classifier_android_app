import tensorflow as tf
from keras.models import load_model

model = load_model("tf_model.h5")
converter = tf.lite.TFLiteConverter.from_keras_model(model)

lite_model = converter.convert()
with open("lite_model.tflite", "wb") as f:
  f.write(lite_model)
