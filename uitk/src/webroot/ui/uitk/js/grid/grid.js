define( [ "jquery",
          "uif/js/clarity",
          "uitk/js/grid/select",
          "uitk/js/grid/FixedHeader" ], function( $, $c ) {

  $c.uitk.grid = $c.uitk.grid || {};
  $c.uitk.grid.headers = [];
  $c.uitk.grid.processing = false;
  $c.uitk.grid.addedResizeListener = false;

  /**
   * Grid constructor
   * @param {Object} the element that represents grid content in the DOM
   */
  $c.uitk.grid.Obj = function(grid) {
          this.grid = grid;
          //this.header = new $c.uitk.grid.StickyHeader(grid); // give sticky what it wants...grid here is entire table
          //this.select = new $c.uitk.grid.Select($(grid).find("tbody.ppm_grid_content").get(0));
  };
  
  /**
   * Loops through all grids on page and initializes them.
   * Also 
   * 
   * @param {String} the current location (Workspace, MNP, or Modal)
   * @param {Object} the root DOM element of then current location
   */
  $c.uitk.grid.init = function(location,root) {
    FixedHeader.afnScroll = [];
    var array = new Array()
    var grids = $(root).find("table.ppm_grid,table.ppm_list_layout");
    for( var i = 0; i < grids.length; i++ ) {
  	  array[i] = new $c.uitk.grid.Obj(grids.get(i)); // change this caching to map instead of array
	  }
    $(root).find(".ppm_grid tbody tr").each(function(){
      $(this).append("<div id=\"clickToSocial\"><img src=\"/niku/ui/uitk/images/clickToSocial.jpg\" height=\"15\" width=\"15\"></div>");
      $(this).find("#clickToSocial").toggle(function() {
        var objectCode = $('.ppm_gridcontent div:first-child').attr('objectcode');
        var instanceId = $(this).parent().attr('data-ppm_odf_pk');
        $("#listViewObjectCode").val(objectCode);
        $("#listViewInstanceId").val(instanceId);
        $(this).find("img").attr("src","/niku/ui/uitk/images/collapseSocial.jpg");
        var token = $c.session.username+'%3A'+$c.session.username+'%3Aappid%3Acont%3Aurl%3A0%3Adefault&scrolling=no&nocache';
        var url = 'http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgetObjectContext.xml';
        var ifrUrl = 'http://localhost/niku/gadgets/ifr' + '?url=' + url + '&st=' + token;
        var height =  $("#ppm_app").outerHeight() ;
        var width = $("#ppm_app").outerWidth() - $("#socialmenuifr").outerWidth();
        var body_width = $("body").outerWidth();

        var socialctxifr_width = body_width / 2 ;

        var ppm_app_width =  body_width / 2 - $("#socialmenuifr").outerWidth() ;
//        if( width % 2 == 1 )
//        {
//          socialctxifr_width = (width-1)/2;
//          ppm_app_width = socialctxifr_width +1;
//        }
//        else
//        {
//          socialctxifr_width = (width)/2;
//          ppm_app_width = socialctxifr_width;
//        }
//        ppm_app_width -= 14;
        $("#socialctx").css({'width': socialctxifr_width});
        $("#socialctxifr").animate({'min-width':'100%','width': socialctxifr_width, 'max-height':height + 'px', 'height':height + 'px', 'display':'inline-block'}, 100);
        $("#socialctxifr").attr('src', ifrUrl);
        $("#ppm_app").css({'width': ppm_app_width});

      }, function() {
        $(this).find("img").attr("src","/niku/ui/uitk/images/clickToSocial.jpg");
        $("#socialctxifr").animate({'width':'0'}, 100);
        $("#socialctxifr").attr('src', '');
        $("#ppm_app").css({'width':''});
      });
    })


    // no more fixed header as separate from grid...make it part of the grid lifecycle
    $c.uitk.grid.initFixedHeader(location,root);
    return array;
  };
  
  $c.uitk.grid.cleanHeader = function() {
    $c.uitk.grid.headers = [];
    $(FixedHeader.afnScroll).empty();
    $($c.uitk.grid.headers).empty();
    $(".fixedHeader").each( function(index, element) {
      $(element).remove();
      FixedHeader.afnScroll = [];//clearing out all Fixed Headers.
    });
  };

  /**
   * Initializes the fixed column header to each grid.
   * 
   * @param {String} the current location (Workspace, MNP, or Modal)
   * @param {Object} the root DOM element of then current location
   */
  
  $c.uitk.grid.initFixedHeader = function(location,root) {
    if( $c.session.screenReaderOpt ) {
      return;
    }
    var isIE9 = false;
    var ua = navigator.userAgent.toLowerCase();
    if(navigator.userAgent.indexOf("Trident/5") > -1)
    {
      isIE9 = true;
    }
    if( ua.indexOf("msie 8.") > -1 && ! isIE9 ) {
      return;
    }
    if( ua.indexOf("msie 7.") > -1 && ! isIE9) {
      return;
    }
    if( ua.indexOf("msie 6.") > -1 && ! isIE9) {
      return;
    }
    $c.uitk.grid.cleanHeader();
   
    var array = new Array();
    var absolute = false;

    if (root == null)
      root = $("#ppm_app");
    var gridheaders = $(root).find("table.ppm_grid > thead,table.ppm_list_layout > thead");
    $(gridheaders).parent().each( function( index, element ) {
      if (element.hasAttribute("noFixedHeader"))
      {
    	  return;
      }
      var scrollPane = $(root).find( ".ppm_page_content" );
      var header = new FixedHeader( element, { "nScrollPane" : scrollPane, "zTop" : 100, "bUseAbsoluteMode" : absolute, "nOriginalThead" : gridheaders[index], "nOriginalTable" : element} );
      $c.uitk.grid.headers.push( header ); 
    });

    $(root).find( ".ppm_page_content").scroll( function() {
      FixedHeader.fnMeasure();
      var len = FixedHeader.afnScroll.length;
      for ( var i=0; i<len; i++ ) {
        FixedHeader.afnScroll[i]();
      }
    });

    if (!$c.uitk.grid.addedResizeListener)
    {
      jQuery(window).resize( function () {
    	$.event.trigger({
          type: "resizeHeaderColumns"
    	});
      } );
      $(document).on("resizeHeaderColumns", function() {
        var len = $c.uitk.grid.headers.length;
        FixedHeader.fnMeasure();
        for (var i=0; i<len; i++)
        {
          var header = $c.uitk.grid.headers[i];
          header._fnUpdateClones.call(header);
          header._fnUpdatePositions.call(header);
        }
      });
      $c.uitk.grid.addedResizeListener = true;
    }
  };

  /**
   * Method to initialize Grid Headers if the location of the original page is known.
   * Location could be: Workspace, MNP, Modal
   * This method is currently being used in two places 
   * 	1. When user closes a dialog without refreshing the parent, the parent needs the headers rebuild.
   * 	2. When user expand/paginate a HGrid the table needs the headers rebuild.
   */
  $c.uitk.grid.rebuildHeaders = function(location) {
    $c.uitk.grid.headers = [];
      switch( location )
      {
      	case "MNP":
      		$elementRoot = $("#ppm_mnp").get(0);
      		break;
      	case "Modal":
      		$elementRoot = $("#ppm_dialog").get(0);
      		break;
        default:
     	   $elementRoot = $("#ppm_workspace").get(0);
     	   break;
      }
     $c.uitk.grid.init(location, $elementRoot); // Initialize the Floating Headers in the Grid.
     
     // Added code to fix CLRT-70510 . On grid level next page or TSV next page 
     // the additional button were showing up. Seen this issue with dialogs
     // hence isolating the change only for dialogs. 
     if(location == "MNP" || location == "Modal")
     {
    	var dialog = $(".ppm_dialog");
    	
    	if(dialog && dialog.length > 0)
    	{
            $(".ppm_dialog").resize();
    	}
     }
  };
  
 
  
  
  /**
   * Method to clear visual indicator for copy command
   */
  $c.uitk.grid.clearCopySelection = function() {
      var selectedCells = $('td.ppm_grid_cell_selected');
      $(selectedCells).find('div').css('backgroundImage','');
      $(selectedCells).find( 'div > div').css('backgroundColor','');
  };

  /**
   * Method to set visual indicator for copy command
   */
  $c.uitk.grid.setCopySelection = function() {
      var selectedCells = $('td.ppm_grid_cell_selected');
      var bgColor = $(selectedCells).first().css('backgroundColor');
      $.each(selectedCells, function () {
        var outerDivs = $(this).find('div');
        var innerDivs = $(this).find( 'div > div');
        if ( $(outerDivs).length == 0 && $(innerDivs).length == 0)
        {
      	  var innerHtml = $(this).html();
          $(this).empty();
          $(this).append("<div> <div> " + innerHtml + " </div> </div>");
          outerDivs = $(this).find('div');
          innerDivs = $(this).find( 'div > div');
        }
        var tdHeight = $(this).closest('td').height();
        $(outerDivs).height(tdHeight);
        $(outerDivs).css('backgroundImage','url("ui/uitk/images/selection.gif")');
        $(innerDivs).css({'backgroundColor': bgColor,backgroundImage:''});
      });
  };

  $c.uitk.grid.resizeHeaderColumns = function() {
	$.event.trigger({
        type: "resizeHeaderColumns"
  	});
  };

  /**
   * Method to determine if copy buffer can be pasted from currently selected cell
   */
  $c.uitk.grid.enoughRoomForPaste = function(x, y) {
    var selectedCells = $('td.ppm_grid_cell_selected');
    if ($(selectedCells).length == 1)
    {
      var originationCell = $(selectedCells).first();
      var originatonRow = $(originationCell).attr("row");
      var originatonCol = $(originationCell).attr("column");
      var destinationRow = (originatonRow*1) + y-1;
      var destinationCol = (originatonCol*1) + x-1;
      var destinationCell = $('td.ppm_tsv_cell[row="'+ destinationRow +'"][column="'+ destinationCol +'"]');
      if ($(destinationCell).length == 1)
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    return false;
  };

  $c.uitk.grid.setUndoArea = function() {
    var selectedCells = $('td.ppm_grid_cell_selected');
    $(selectedCells).addClass('ppm_grid_undo_area');
  };
  
  $c.uitk.grid.hasUndoArea = function() {
    var selectedCells = $('td.ppm_grid_undo_area');
    if ($(selectedCells).length < 1)
    {
      return false;
    }
    else
    {
      return true;
    }
  };

  $c.uitk.grid.clearUndoArea = function() {
    var selectedCells = $('td.ppm_grid_undo_area');
    $(selectedCells).removeClass('ppm_grid_undo_area');
  };

  $c.uitk.grid.maxRowSelected = function(){
    var selectedCells = $('td.ppm_grid_cell_selected');
    var maxRowsSelected = parseInt(-1);
    $.each(selectedCells, function (){
       if($(this).attr('row') != 'undefined' && parseInt($(this).attr('row')) + 1 > maxRowsSelected)
       {
    	 maxRowsSelected = parseInt($(this).attr('row')) -1; 
       }
    });
    return maxRowsSelected;
  };

});