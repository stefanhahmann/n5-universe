/**
 * Copyright (c) 2018--2020, Saalfeld lab
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.janelia.saalfeldlab.n5.universe.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.janelia.saalfeldlab.n5.N5Reader;
import org.janelia.saalfeldlab.n5.universe.N5DatasetDiscoverer;
import org.janelia.saalfeldlab.n5.universe.N5TreeNode;

public class N5ViewerMultiscaleMetadataParser implements N5MetadataParser< N5MultiScaleMetadata >
{

  /**
   * Called by the {@link N5DatasetDiscoverer}
   * while discovering the N5 tree and filling the metadata for datasets or groups.
   *
   * @param reader the n5 reader
   * @param node   the node
   * @return the metadata
   */
  @Override public Optional<N5MultiScaleMetadata> parseMetadata(final N5Reader reader, final N5TreeNode node) {

	final Map<String, N5TreeNode> scaleLevelNodes = new HashMap<>();
	for (final N5TreeNode childNode : node.childrenList()) {
	  if ( MultiscaleMetadata.scaleLevelPredicate.test(childNode.getNodeName()) &&
			  childNode.isDataset() &&
			  childNode.getMetadata() instanceof N5SingleScaleMetadata ) {
		scaleLevelNodes.put(childNode.getNodeName(), childNode);
	  }
	}

	if (scaleLevelNodes.isEmpty())
	  return Optional.empty();

	final N5SingleScaleMetadata[] childMetadata = scaleLevelNodes.values().stream().map(N5TreeNode::getMetadata).toArray(N5SingleScaleMetadata[]::new);
	MultiscaleMetadata.sortScaleMetadata(childMetadata);
	return Optional.of(new N5MultiScaleMetadata(node.getPath(), childMetadata));
  }

}
