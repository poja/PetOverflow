
(function (angular, d3) {

	var app = angular.module('petd3', ['nvd3']);

	app.directive('tagCloud', function () {
		return {
			restrict: 'E',
			scope: {
				options: '=',
				data: '='
			},
			link: function (scope, element, attrs, controller) {

				/**
				 * @param arr<string> 	words - an array of words to be in the tag cloud
				 * @param string		goalElement	- selector, the cloud will be inside it
				 */
				function drawTagCloud(words) {
					
					var maxSize = words.reduce(function (prevValue, currentEl) {
						return (prevValue > currentEl.size) ? prevValue : currentEl.size;
					}, 0);
					var minSize = words.reduce(function (prevValue, currentEl) {
						return (prevValue < currentEl.size) ? prevValue : currentEl.size;
					}, 0);
					function pxSize(word) {
						var relativeSize = ((word.size - minSize + 0.5) / (maxSize - minSize + 0.5)) || 0;
						debugger;
						return relativeSize * 20 + 25; // min > 10, max = 15
					}

				    function drawingCallback(processedWords) {
				    	d3.select(element[0])
				    		.append('svg')
				        		.attr('height', 300)
				      		.append('g')
				        		.attr('transform', 'translate(150,150)')
				      		.selectAll('a')
				        	.data(processedWords)
				     		.enter().append('a')
				     			.attr('xlink:href', function (d, i) { return words[i].url; })
				     			.append('text')
					       			.style('font-size', function(d) { return d.size + 'px'; })
					        		.style('font-family', 'Impact')
					        		// .style('fill', function(d, i) { console.log(fill(i)); return fill(i); })
					        		.style('opacity', function() { return Math.random()/2 + 0.5})
						        	.attr('text-anchor', 'middle')
						        	.attr('transform', function(d) {
						          		return 'translate(' + [d.x, d.y] + ')rotate(' + d.rotate + ')';
						        	})
						        	.text(function(d) { return d.text; });
				    }
				    d3.layout.cloud().size([300, 300])
				    	.words(words) // using the given parameter
				      	.rotate(function() { return ~~(Math.random() * 2) * 90; })
				      	.font('Impact')
				      	.fontSize(pxSize) 
				      	.on('end', drawingCallback)
				      	.start();
			  	}
			  	drawTagCloud(scope.data);
			}
		};
	});


})(angular, d3);