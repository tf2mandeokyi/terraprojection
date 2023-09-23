# TerraProjection

TerraProjection is a lightweight projection dependency that is a stripped-down version of
[Terra++](https://github.com/BuildTheEarth/terraplusplus).

TerraProjection replaces all the [Jackson](https://github.com/FasterXML/jackson) (JSON library) related codes with
[GSON](https://github.com/google/gson) to further lighten its dependencies since GSON is already in Minecraft and the 
shaded Jackson library is nearly 10 times bigger than GSON.