$("#searchResults").hide();
fillFloorFromValues();
fillFloorToValues();


$(".submit").click(function() {
    getResults();
});


$("#searchText").keyup(function(event){
    if(event.keyCode == '13'){
        getResults();
    }
});

function getResults() {
    var searchText = $('#searchText').val();
    var address = $('#address').val();
    var areaFrom = $('#areaFrom').val();
    var areaTo = $('#areaTo').val();
    var floorFrom = $( "#floorFrom option:selected" ).text();
    var floorTo = $( "#floorТо option:selected" ).text();
    $.ajax({
      type : "GET",
      url : "rest/search?searchText="+searchText+"&address="+address+"&areaFrom="+areaFrom+"&areaTo="+areaTo+"&floorFrom="+floorFrom+"&floorTo="+floorTo,
      success : function(data) {
        var invitations = '';
            if(data.length == 0){
                checkSpelling(searchText);
            }else{
                $("#spellSuggestion").hide();
            }
            for(var i=0;i<data.length;i++){
                var content = data[i].content;
              content = truncateText(content,200);
                invitations=invitations.concat('<article class="search-result row"><div class="col-xs-12 col-sm-12 col-md-3"><a href="#" class="thumbnail"><img src="./logo.png" /></a></div><div class="col-xs-12 col-sm-12 col-md-2"><ul class="meta-search"><li><i class="glyphicon glyphicon-usd"></i> <span>Цена: ' +data[i].price+'</span></li><li><i class="glyphicon glyphicon-home"></i> <span>Квадратура: ' +data[i].area+'</span></li><li><i class="glyphicon glyphicon-th-large"></i> <span>Етаж: '+data[i].floor+'</span></li><li><i class="glyphicon glyphicon-earphone"></i> <span>Телефон: '+data[i].telephone+'</span></li></ul></div><div class="col-xs-12 col-sm-12 col-md-7 excerpet"><h3><a target="_blank" href="'+data[i].url+'" title="">'+data[i].title+'</a></h3><p>'+content+'</p></div><span class="clearfix borda"></span></article>');
            }
            var node = document.getElementById('advertisments');
            node.innerHTML=invitations;

          var searchInfo = document.getElementById('searchInformation');
          searchInfo.innerHTML='<strong class="text-danger">'+data.length+'</strong> results were found for the search for <strong class="text-danger">'+searchText+'</strong>';

          $("#searchResults").show();
      },
      error: function(xhr, textStatus, errorThrown){
          alert(textStatus);
          alert(errorThrown);
      }
    });
}

function checkSpelling(spellcheckText) {
    var address = $('#address').val();
    var areaFrom = $('#areaFrom').val();
    var areaTo = $('#areaTo').val();
    var floorFrom = $( "#floorFrom option:selected" ).text();
    var floorTo = $( "#floorТо option:selected" ).text();
    var spellSuggestion = document.getElementById('spellSuggestion');
    $.ajax({
      type : "GET",
      url : "rest/spellcheck?misspellText="+spellcheckText+"&address="+address+"&areaFrom="+areaFrom+"&areaTo="+areaTo+"&floorFrom="+floorFrom+"&floorTo="+floorTo,
      success : function(suggestion) {
          if(suggestion){
            spellSuggestion.innerHTML='<a onclick="clickOnSuggestion(\''+suggestion+'\')" href="#">Do you mean: ' + suggestion+'</a>';
            $("#spellSuggestion").show();
          }
      },
      error: function(xhr, textStatus, errorThrown){
          alert(textStatus);
          alert(errorThrown);
      }
    });
}

function clickOnSuggestion(suggestionText){
    document.getElementById('searchText').value = suggestionText;
    getResults();
}
function truncateText(text, maxLength) {
    if (text.length > maxLength) {
        text = text.substr(0,maxLength) + '...';
    }
    return text;
}

function fillFloorFromValues(){
    var sel = document.getElementById('floorFrom');
    var opt = document.createElement('option');
    opt.innerHTML = "Партер";
    opt.value = 1;
    sel.appendChild(opt);
    for(var i = 2; i < 28; i++) {
        var opt = document.createElement('option');
        opt.innerHTML = i-1;
        opt.value = i;
        sel.appendChild(opt);
    }
}

function fillFloorToValues(){
    var sel = document.getElementById('floorТо');
    var opt = document.createElement('option');
    opt.innerHTML = "Партер";
    opt.value = 1;
    sel.appendChild(opt);
    for(var i = 2; i < 28; i++) {
        var opt = document.createElement('option');
        opt.innerHTML = i-1;
        opt.value = i;
        sel.appendChild(opt);
    }
}