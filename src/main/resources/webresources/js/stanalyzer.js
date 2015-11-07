var plot1 = null;


/* Formatting function for row details - modify as you need */
function format ( d ) {
    // `d` is the original data object for the row
    return 
    '<div class="slider">'+
        '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'+
            '<tr>'+
                '<td>Full name:</td>'+
                '<td>ssss</td>'+
            '</tr>'+
            '<tr>'+
                '<td>Extension number:</td>'+
                '<td>fvfgvf</td>'+
            '</tr>'+
            '<tr>'+
                '<td>Extra info:</td>'+
                '<td>And any further details here (images etc)...</td>'+
            '</tr>'+
        '</table>'+
    '</div>';
//    '<div class="slider"><div class="slider-loading">o</div></div>';
}

function fetchAjaxData(url, success) {
    $.ajax({
    	url: "stan",
        type:"GET",
        dataType: "json",
        success: function(data) {
            success(data);
            console.log('loaded');
        },
        error : function() {
        	console.log("error");
        }
    });
}

function createPlot(url) {
    fetchAjaxData(url, function(data) {
    	var prices = [[]];
    	
    	for(var i = 0; i < data.cene.length; i++) {
    		
    		prices[0].push([new Date(data.cene[i]["datumOd"]),data.cene[i]["iznos"]])
    		
    		
    		}
        if (plot1 == null) {
            plot1 = $.jqplot('chart1', prices, {
                title: data.sajt.toUpperCase()+" - (" + data.lokacija + ", " + data.kvadratura + "m2)",
                animate: true,
                // Will animate plot on calls to plot1.replot({resetAxes:true})
                animateReplot: true,
                
                series : [{

                        rendererOptions: {
                            // speed up the animation a little bit.
                            // This is a number of milliseconds.
                            // Default for a line series is 2500.
                            animation: {
                                speed: 1500
                            }
                        }

                }],
                axes:{
                    xaxis:{
                      renderer:$.jqplot.DateAxisRenderer,
                      tickOptions:{
                    	  formatString:'%b %#d, %y',
                      },
                      rendererOptions: {
                          daTickInterval: "1 day"
                    	}
                    },
                    yaxis:{
                      tickOptions:{
                        formatString:'$%.2f'
                        }
                    }
                  },
                  highlighter: {
                    show: true,
                    sizeAdjust: 9.5
                  },
                cursor: {
                	show: false
                },
            });
        } else {
            plot1.replot({data: prices});
            console.log('replotting');
        }
    });
}

$(document).ready(function(){

    //Regenerate the plot on button click.
    $('#chart_left_navigation,#chart_right_navigation').click(function() {
        createPlot("stan");
    });
    
    var table = $('#example').DataTable( {
        "ajax": "stan",
        "processing": true,
        "serverSide": true,
        "columns": [
                     {
                        "class":          'details-control',
                        "orderable":      false,
                        "data":           null,
                        "defaultContent": ''
                    }, 
            { "data": "id" },
            { "data": "lokacija" },
            { "data": "poslednjaCena" },
            { "data": "kvadratura" },
            { "data": "sajt" },
            { "data": "link" },
            { "data": "promenaJedan" },

        ],
        "createdRow": function ( row, data, index ) {
            if ( data["promenaJedan"] > 0) {
                $(row).addClass('danger');
            }
            else if ( data["promenaJedan"] < 0) {
                $(row).addClass('info');
            }
            var linkCell = $(row).find("td").eq(5);
            linkCell.empty();
            linkCell.append("<a href='"+linkCell.text()+"'> Link </a>");
        }
    } );
    
 // Add event listener for opening and closing details
    $('#example tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );
 
        if ( row.child.isShown() ) {
            // This row is already open - close it
            $('div.slider', row.child()).slideUp( function () {
                row.child.hide();
                tr.removeClass('shown');
            } );
        }
        else {
            // Open this row
            row.child( format(row.data()), 'no-padding' ).show();
            tr.addClass('shown');
 
            $('div.slider', row.child()).slideDown();
        }
    } );
});