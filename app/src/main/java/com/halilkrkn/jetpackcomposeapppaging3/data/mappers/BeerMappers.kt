package com.halilkrkn.jetpackcomposeapppaging3.data.mappers

import com.halilkrkn.jetpackcomposeapppaging3.data.local.BeerEntity
import com.halilkrkn.jetpackcomposeapppaging3.data.remote.BeerDto
import com.halilkrkn.jetpackcomposeapppaging3.domain.Beer

fun BeerDto.toBeerEntity() = BeerEntity(
    id = id,
    name = name,
    tagline = tagline,
    description = description,
    firstBrewed = first_brewed,
    imageUrl = image_url
)

fun BeerEntity.toBeer() = Beer(
    id = id,
    name = name,
    tagline = tagline,
    description = description,
    firstBrewed = firstBrewed,
    imageUrl = imageUrl
)