/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.core.query.selection.iterator;

import com.linkedin.pinot.common.data.FieldSpec;
import com.linkedin.pinot.common.utils.primitive.ByteArray;
import com.linkedin.pinot.core.segment.index.readers.Dictionary;
import java.io.Serializable;

import com.linkedin.pinot.core.common.Block;
import com.linkedin.pinot.core.common.BlockSingleValIterator;


/**
 * Iterator on single-value column with dictionary for selection query.
 *
 */
public class SelectionSingleValueColumnWithDictIterator implements SelectionColumnIterator {
  protected BlockSingleValIterator _blockSingleValIterator;
  protected Dictionary _dictionary;
  private final FieldSpec.DataType _dataType;

  public SelectionSingleValueColumnWithDictIterator(Block block) {
    _blockSingleValIterator = (BlockSingleValIterator) block.getBlockValueSet().iterator();
    _dataType = block.getMetadata().getDataType();
    _dictionary = block.getMetadata().getDictionary();
  }

  @Override
  public Serializable getValue(int docId) {
    _blockSingleValIterator.skipTo(docId);

    // For selection, we convert BYTES data type to equivalent HEX string.
    if (_dataType.equals(FieldSpec.DataType.BYTES)) {
      return ByteArray.toHexString(_dictionary.getSerializedValue(_blockSingleValIterator.nextIntVal()));
    }
    return (Serializable) _dictionary.get(_blockSingleValIterator.nextIntVal());
  }
}
