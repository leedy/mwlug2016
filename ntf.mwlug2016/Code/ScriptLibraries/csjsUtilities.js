var timeoutID; 
function setAutoRefresh(refreshTime, relativePath) { 
        if(timeoutID){ 
                window.clearTimeout(timeoutID); 
        } 
        timeoutID = window.setTimeout(function(){ location.replace(relativePath);}, refreshTime); 
} 