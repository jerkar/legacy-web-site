		</div>
		<div id="push"></div>
    </div>
    
    <div id="footer">
      <div class="container">
        <p class="muted credit">&copy; 2014 | Mixed with <a href="http://getbootstrap.com/">Bootstrap v3.1.1</a> | Baked with <a href="http://jbake.org">JBake ${version}</a></p>
      </div>
    </div>
    
   
    
    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/jquery-1.11.1.min.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/bootstrap.min.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/prettify.js"></script>
 	<script>
 	    lastSelect = null;
    	$('.liexpandable').click(function(evt) {
    		var li = $(evt.target).parents("li").first();
    		var ul = li.find(".sub-menu").first();
    		console.log(ul.is(':visible'));
    		evt.stopPropagation();
    		if (ul.is(':visible') && li.html() !== lastSelect) {
    			lastSelect = li.html();
    			return;
    		}
    		lastSelect = li.html();
      		ul.toggle();
 		});
    </script>
    
  </body>
</html>