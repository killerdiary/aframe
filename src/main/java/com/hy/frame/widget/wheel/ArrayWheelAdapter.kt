/*
 *  Copyright 2010 Yuri Kanivets
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hy.frame.widget.wheel

import com.hy.frame.adapter.IWheelAdapter

/**
 * The simple Array wheel adapter
 * @param items the items
 * @param length the max items length
 */
class ArrayWheelAdapter @JvmOverloads constructor(private val items: List<String>?, override val maximumLength: Int = ArrayWheelAdapter.DEFAULT_LENGTH) : IWheelAdapter {

    override fun getItem(index: Int): String? {
        if (index >= 0 && index < items!!.size) {
            return items[index]
        }
        return null
    }

    override val itemsCount: Int
        get() = items?.size ?: 0

    companion object {
        /** The default items length  */
        val DEFAULT_LENGTH = -1
    }

}