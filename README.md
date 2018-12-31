# GISImgFiltersApp
A very simple app to apply various filters to images. It uses https://github.com/emonm/AndroidPhotoFilters as low level filter implementation, and https://github.com/ArkadyGamza/Camera2API_rxJava2 as the code base for camera module. 

The main puprpose of this project is to provide Camera module, which can be copied to any Android project if it needs custom implementation of camera. Camera module implemented using Kotlin, Camera2 API, MVI approach, RxJava2(completely reactive - no callbacks) and Fragment as a view controller.
