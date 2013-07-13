(function() {
	// we need tabs as spaces and not CSS magin-left
	// in order to ratain format when coping and pasing the code
	var SINGLE_TAB = "  ";
	var ImgCollapsed = "images/Collapsed.gif";
	var ImgExpanded = "images/Expanded.gif";

	// utility methods
	function $id(id) {
		return document.getElementById(id);
	}
	function IsArray(obj) {
		return obj &&
						typeof obj === 'object' &&
						typeof obj.length === 'number' &&
						!(obj.propertyIsEnumerable('length'));
	}

	QuickJSONFormatter = (function() {
		var QuoteKeys = true;
		QuickJSONFormatter.prototype.Process = function() {
			SetTab();
			window.IsCollapsible = $id("CollapsibleView").checked;
			var json = $id("RawJson").value;
			var html = "";
			try{
				if (json == "") json = "\"\"";
				var obj = eval("["+json+"]");
				html = ProcessObject(obj[0], 0, false, false, false);
				$id("Canvas").innerHTML = "<PRE class='CodeContainer'>"+html+"</PRE>";
			}catch(e) {
				alert("JSON is not well formated:\n"+e.message);
				$id("Canvas").innerHTML = "";
			}
		};
		var _dateObj = new Date();
		var _regexpObj = new RegExp();
		QuickJSONFormatter.prototype.ProcessObject = function(obj, indent, addComma, isArray, isPropertyContent) {
			var html = "";
			var comma = (addComma) ? "<span class='Comma'>,</span> " : "";
			var type = typeof obj;
			var clpsHtml ="";
			if (IsArray(obj)) {
				if (obj.length == 0) {
					html += GetRow(indent, "<span class='ArrayBrace'>[ ]</span>"+comma, isPropertyContent);
				} else {
					clpsHtml = window.IsCollapsible ? "<span><img src=\""+window.ImgExpanded+"\" onClick=\"ExpImgClicked(this)\" /></span><span class='collapsible'>" : "";
					html += GetRow(indent, "<span class='ArrayBrace'>[</span>"+clpsHtml, isPropertyContent);
					for (var i = 0; i < obj.length; i++) {
						html += ProcessObject(obj[i], indent + 1, i < (obj.length - 1), true, false);
					}
					clpsHtml = window.IsCollapsible ? "</span>" : "";
					html += GetRow(indent, clpsHtml+"<span class='ArrayBrace'>]</span>"+comma);
				}
			} else if (type == 'object') {
				if (obj == null) {
						html += FormatLiteral("null", "", comma, indent, isArray, "Null");
				} else if (obj.constructor == window._dateObj.constructor) {
						html += FormatLiteral("new Date(" + obj.getTime() + ") /*" + obj.toLocaleString()+"*/", "", comma, indent, isArray, "Date");
				} else if (obj.constructor == window._regexpObj.constructor) {
						html += FormatLiteral("new RegExp(" + obj + ")", "", comma, indent, isArray, "RegExp");
				} else {
					var numProps = 0;
					for (var prop in obj) numProps++;
					if (numProps == 0) {
						html += GetRow(indent, "<span class='ObjectBrace'>{ }</span>"+comma, isPropertyContent);
					} else {
						clpsHtml = window.IsCollapsible ? "<span><img src=\""+window.ImgExpanded+"\" onClick=\"ExpImgClicked(this)\" /></span><span class='collapsible'>" : "";
						html += GetRow(indent, "<span class='ObjectBrace'>{</span>"+clpsHtml, isPropertyContent);
						var j = 0;
						for (var prop in obj) {
							var quote = window.QuoteKeys ? "\"" : "";
							html += GetRow(indent + 1, "<span class='PropertyName'>"+quote+prop+quote+"</span>: "+ProcessObject(obj[prop], indent + 1, ++j < numProps, false, true));
						}
						clpsHtml = window.IsCollapsible ? "</span>" : "";
						html += GetRow(indent, clpsHtml+"<span class='ObjectBrace'>}</span>"+comma);
					}
				}
			} else if (type == 'number') {
				html += FormatLiteral(obj, "", comma, indent, isArray, "Number");
			} else if (type == 'boolean') {
				html += FormatLiteral(obj, "", comma, indent, isArray, "Boolean");
			} else if (type == 'function') {
				if (obj.constructor == window._regexpObj.constructor) {
						html += FormatLiteral("new RegExp(" + obj + ")", "", comma, indent, isArray, "RegExp");
				} else {
						obj = FormatFunction(indent, obj);
						html += FormatLiteral(obj, "", comma, indent, isArray, "Function");
				}
			} else if (type == 'undefined') {
				html += FormatLiteral("undefined", "", comma, indent, isArray, "Null");
			} else {
				html += FormatLiteral(obj.toString().split("\\").join("\\\\").split('"').join('\\"'), "\"", comma, indent, isArray, "String");
			}
			return html;
		};
		QuickJSONFormatter.prototype.FormatLiteral = function(literal, quote, comma, indent, isArray, style) {
			if (typeof literal == 'string')
				literal = literal.split("<").join("&lt;").split(">").join("&gt;");
			var str = "<span class='"+style+"'>"+quote+literal+quote+comma+"</span>";
			if (isArray) str = GetRow(indent, str);
			return str;
		};
		QuickJSONFormatter.prototype.FormatFunction = function(indent, obj) {
			var tabs = "";
			for (var i = 0; i < indent; i++) tabs += window.TAB;
			var funcStrArray = obj.toString().split("\n");
			var str = "";
			for (var i = 0; i < funcStrArray.length; i++) {
				str += ((i==0)?"":tabs) + funcStrArray[i] + "\n";
			}
			return str;
		};
		QuickJSONFormatter.prototype.GetRow = function(indent, data, isPropertyContent) {
			var tabs = "";
			for (var i = 0; i < indent && !isPropertyContent; i++) tabs += window.TAB;
			if (data != null && data.length > 0 && data.charAt(data.length-1) != "\n")
				data = data+"\n";
			return tabs+data;
		};
		QuickJSONFormatter.prototype.CollapsibleViewClicked = function() {
			$id("CollapsibleViewDetail").style.visibility = $id("CollapsibleView").checked ? "visible" : "hidden";
			Process();
		};

		QuickJSONFormatter.prototype.QuoteKeysClicked = function() {
			window.QuoteKeys = $id("QuoteKeys").checked;
			Process();
		};

		QuickJSONFormatter.prototype.CollapseAllClicked = function() {
			EnsureIsPopulated();
			TraverseChildren($id("Canvas"), function(element) {
				if (element.className == 'collapsible') {
					MakeContentVisible(element, false);
				}
			}, 0);
		};
		QuickJSONFormatter.prototype.ExpandAllClicked = function() {
			EnsureIsPopulated();
			TraverseChildren($id("Canvas"), function(element) {
				if (element.className == 'collapsible') {
					MakeContentVisible(element, true);
				}
			}, 0);
		};
		QuickJSONFormatter.prototype.MakeContentVisible = function(element, visible) {
			var img = element.previousSibling.firstChild;
			if (!!img.tagName && img.tagName.toLowerCase() == "img") {
				element.style.display = visible ? 'inline' : 'none';
				element.previousSibling.firstChild.src = visible ? window.ImgExpanded : window.ImgCollapsed;
			}
		};
		QuickJSONFormatter.prototype.TraverseChildren = function(element, func, depth) {
			for (var i = 0; i < element.childNodes.length; i++) {
				TraverseChildren(element.childNodes[i], func, depth + 1);
			}
			func(element, depth);
		};
		QuickJSONFormatter.prototype.ExpImgClicked = function(img) {
			var container = img.parentNode.nextSibling;
			if (!container) return;
			var disp = "none";
			var src = window.ImgCollapsed;
			if (container.style.display == "none") {
					disp = "inline";
					src = window.ImgExpanded;
			}
			container.style.display = disp;
			img.src = src;
		};
		QuickJSONFormatter.prototype.CollapseLevel = function(level) {
			EnsureIsPopulated();
			TraverseChildren($id("Canvas"), function(element, depth) {
				if (element.className == 'collapsible') {
					if (depth >= level) {
						MakeContentVisible(element, false);
					} else {
						MakeContentVisible(element, true);
					}
				}
			}, 0);
		};
		QuickJSONFormatter.prototype.TabSizeChanged = function() {
			Process();
		};
		QuickJSONFormatter.prototype.SetTab = function() {
			var select = $id("TabSize");
			window.TAB = MultiplyString(parseInt(select.options[select.selectedIndex].value), window.SINGLE_TAB);
		};
		QuickJSONFormatter.prototype.EnsureIsPopulated = function() {
			if (!$id("Canvas").innerHTML && !!$id("RawJson").value) Process();
		};
		QuickJSONFormatter.prototype.MultiplyString = function(num, str) {
			var sb =[];
			for (var i = 0; i < num; i++) {
				sb.push(str);
			}
			return sb.join("");
		};
		QuickJSONFormatter.prototype.SelectAllClicked = function() {

			if (!!document.selection && !!document.selection.empty) {
				document.selection.empty();
			} else if (window.getSelection) {
				var sel = window.getSelection();
				if (sel.removeAllRanges) {
					window.getSelection().removeAllRanges();
				}
			}

			var range =
					(!!document.body && !!document.body.createTextRange)
							? document.body.createTextRange()
							: document.createRange();

			if (!!range.selectNode)
				range.selectNode($id("Canvas"));
			else if (range.moveToElementText)
				range.moveToElementText($id("Canvas"));

			if (!!range.select)
				range.select($id("Canvas"));
			else
				window.getSelection().addRange(range);
		};
		QuickJSONFormatter.prototype.LinkToJson = function() {
			var val = $id("RawJson").value;
			val = escape(val.split('/n').join(' ').split('/r').join(' '));
			$id("InvisibleLinkUrl").value = val;
			$id("InvisibleLink").submit();
		};

	    return QuickJSONFormatter;

	})();

}).call(this);