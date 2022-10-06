(function() {
	// utility methods
	function $id(id) {
		return document.getElementById(id);
	}
	function IsArray(obj) {
		return obj
				&& typeof obj === 'object'
				&& typeof obj.length === 'number'
				&& !(obj.propertyIsEnumerable('length'));
	}
	function Extend(a, b) {
		for(var key in b)
			if(b.hasOwnProperty(key))
				a[key] = b[key];
		return a;
	}

	QuickJSONFormatter = (function() {
		var _dateObj = new Date();
		var _regexpObj = new RegExp();

		function QuickJSONFormatter(options) {
			this.Options = Extend({
				RawJsonId: undefined,
				CanvasId: undefined,
				QuoteKeys: true,
				ImgCollapsed: "images/Collapsed.gif",
				ImgExpanded: "images/Expanded.gif",
				IsCollapsible: true,
				TabSize: 3,
				// we need tabs as spaces and not CSS magin-left
				// in order to ratain format when coping and pasing the code
				SingleTab: "  ",
				OnError: function(e) {
					alert("JSON is not well formated:\n" + e.message);
				}
			}, options);
			if(this.Options.CanvasId === undefined) {
				throw "Please specify CanvasId as the id of the target div to put the tree representation to.";
			} else {
				$id(this.Options.CanvasId).className += " qjf-Canvas";
			}
			QuickJSONFormatter.instances[this.InstanceIndex = ++QuickJSONFormatter.instances_last] = this;
		}
		QuickJSONFormatter.instances = new Array();
		QuickJSONFormatter.instances_last = -1;

		QuickJSONFormatter.prototype.GetRawJsonElement = function() {
			if(!this.Options.RawJsonId || !$id(this.Options.RawJsonId)) {
				throw "Please specify RawJsonId as the id (currently '" + this.Options.RawJsonId+ "')"
					+ " of the source textarea to load the text from; OR provide an optional JSON string as an argument.";
			}
			return $id(this.Options.RawJsonId);
		};
		QuickJSONFormatter.prototype.Process = function(json) {
			json = json || this.GetRawJsonElement().value;
			this.SetTab();
			var html = "";
			try {
				if (json == "") json = "\"\"";
				var obj = eval("["+json+"]");
				html = this.ProcessObject(obj[0], 0, false, false, false);
				$id(this.Options.CanvasId).innerHTML = "<PRE class='qjf-CodeContainer'>"+html+"</PRE>";
			} catch(e) {
				this.Options.OnError(e);
				$id(this.Options.CanvasId).innerHTML = "";
			}
		};
		QuickJSONFormatter.prototype.ProcessObject = function(obj, indent, addComma, isArray, isPropertyContent) {
			var html = "";
			var comma = (addComma) ? "<span class='qjf-Comma'>,</span> " : "";
			var type = typeof obj;
			var clpsHtml ="";
			if (IsArray(obj)) {
				if (obj.length == 0) {
					html += this.GetRow(indent, "<span class='qjf-ArrayBrace'>[ ]</span>"+comma, isPropertyContent);
				} else {
					clpsHtml = this.Options.IsCollapsible ? "<span><img src=\""+this.Options.ImgExpanded+"\" onClick=\"QuickJSONFormatter.instances["+this.InstanceIndex+"].ExpImgClicked(this)\" /></span><span class='qjf-collapsible'>" : "";
					html += this.GetRow(indent, "<span class='qjf-ArrayBrace'>[</span>"+clpsHtml, isPropertyContent);
					for (var i = 0; i < obj.length; i++) {
						html += this.ProcessObject(obj[i], indent + 1, i < (obj.length - 1), true, false);
					}
					clpsHtml = this.Options.IsCollapsible ? "</span>" : "";
					html += this.GetRow(indent, clpsHtml+"<span class='qjf-ArrayBrace'>]</span>"+comma);
				}
			} else if (type == 'object') {
				if (obj == null) {
						html += this.FormatLiteral("null", "", comma, indent, isArray, "qjf-Null");
				} else if (obj.constructor == _dateObj.constructor) {
						html += this.FormatLiteral("new Date(" + obj.getTime() + ") /*" + obj.toLocaleString()+"*/", "", comma, indent, isArray, "qjf-Date");
				} else if (obj.constructor == _regexpObj.constructor) {
						html += this.FormatLiteral("new RegExp(" + obj + ")", "", comma, indent, isArray, "qjf-RegExp");
				} else {
					var numProps = 0;
					for (var prop in obj) numProps++;
					if (numProps == 0) {
						html += this.GetRow(indent, "<span class='qjf-ObjectBrace'>{ }</span>"+comma, isPropertyContent);
					} else {
						clpsHtml = this.Options.IsCollapsible ? "<span><img src=\""+this.Options.ImgExpanded+"\" onClick=\"QuickJSONFormatter.instances["+this.InstanceIndex+"].ExpImgClicked(this)\" /></span><span class='qjf-collapsible'>" : "";
						html += this.GetRow(indent, "<span class='qjf-ObjectBrace'>{</span>"+clpsHtml, isPropertyContent);
						var j = 0;
						for (var prop in obj) {
							var quote = this.Options.QuoteKeys ? "\"" : "";
							html += this.GetRow(indent + 1, "<span class='qjf-PropertyName'>"+quote+prop+quote+"</span>: "+this.ProcessObject(obj[prop], indent + 1, ++j < numProps, false, true));
						}
						clpsHtml = this.Options.IsCollapsible ? "</span>" : "";
						html += this.GetRow(indent, clpsHtml+"<span class='qjf-ObjectBrace'>}</span>"+comma);
					}
				}
			} else if (type == 'number') {
				html += this.FormatLiteral(obj, "", comma, indent, isArray, "qjf-Number");
			} else if (type == 'boolean') {
				html += this.FormatLiteral(obj, "", comma, indent, isArray, "qjf-Boolean");
			} else if (type == 'function') {
				if (obj.constructor == _regexpObj.constructor) {
						html += this.FormatLiteral("new RegExp(" + obj + ")", "", comma, indent, isArray, "qjf-RegExp");
				} else {
						obj = this.FormatFunction(indent, obj);
						html += this.FormatLiteral(obj, "", comma, indent, isArray, "qjf-Function");
				}
			} else if (type == 'undefined') {
				html += this.FormatLiteral("undefined", "", comma, indent, isArray, "qjf-Null");
			} else {
				html += this.FormatLiteral(obj.toString().split("\\").join("\\\\").split('"').join('\\"'), "\"", comma, indent, isArray, "qjf-String");
			}
			return html;
		};
		QuickJSONFormatter.prototype.FormatLiteral = function(literal, quote, comma, indent, isArray, style) {
			if (typeof literal == 'string')
				literal = literal.split("<").join("&lt;").split(">").join("&gt;");
			var str = "<span class='"+style+"'>"+quote+literal+quote+comma+"</span>";
			if (isArray) str = this.GetRow(indent, str);
			return str;
		};
		QuickJSONFormatter.prototype.FormatFunction = function(indent, obj) {
			var tabs = "";
			for (var i = 0; i < indent; i++) tabs += this.TAB;
			var funcStrArray = obj.toString().split("\n");
			var str = "";
			for (var i = 0; i < funcStrArray.length; i++) {
				str += ((i==0)?"":tabs) + funcStrArray[i] + "\n";
			}
			return str;
		};
		QuickJSONFormatter.prototype.GetRow = function(indent, data, isPropertyContent) {
			var tabs = "";
			for (var i = 0; i < indent && !isPropertyContent; i++) tabs += this.TAB;
			if (data != null && data.length > 0 && data.charAt(data.length-1) != "\n")
				data = data+"\n";
			return tabs+data;
		};

		QuickJSONFormatter.prototype.CollapseAll = function() {
			var that = this;
			this.EnsureIsPopulated();
			this.TraverseChildren($id(this.Options.CanvasId), function(element) {
				if (element.className == 'qjf-collapsible') {
					that.MakeContentVisible(element, false);
				}
			}, 0);
		};
		QuickJSONFormatter.prototype.ExpandAll = function() {
			var that = this;
			this.EnsureIsPopulated();
			this.TraverseChildren($id(this.Options.CanvasId), function(element) {
				if (element.className == 'qjf-collapsible') {
					that.MakeContentVisible(element, true);
				}
			}, 0);
		};
		QuickJSONFormatter.prototype.MakeContentVisible = function(element, visible) {
			var img = element.previousSibling.firstChild;
			if (!!img.tagName && img.tagName.toLowerCase() == "img") {
				element.style.display = visible ? 'inline' : 'none';
				element.previousSibling.firstChild.src = visible ? this.Options.ImgExpanded : this.Options.ImgCollapsed;
			}
		};
		QuickJSONFormatter.prototype.TraverseChildren = function(element, func, depth) {
			for (var i = 0; i < element.childNodes.length; i++) {
				this.TraverseChildren(element.childNodes[i], func, depth + 1);
			}
			func(element, depth);
		};
		QuickJSONFormatter.prototype.ExpImgClicked = function(img) {
			var container = img.parentNode.nextSibling;
			if (!container) return;
			var disp = "none";
			var src = this.Options.ImgCollapsed;
			if (container.style.display == "none") {
					disp = "inline";
					src = this.Options.ImgExpanded;
			}
			container.style.display = disp;
			img.src = src;
		};
		QuickJSONFormatter.prototype.CollapseLevel = function(level) {
			var that = this;
			this.EnsureIsPopulated();
			this.TraverseChildren($id(this.Options.CanvasId), function(element, depth) {
				if (element.className == 'qjf-collapsible') {
					if (depth >= level) {
						that.MakeContentVisible(element, false);
					} else {
						that.MakeContentVisible(element, true);
					}
				}
			}, 0);
		};
		QuickJSONFormatter.prototype.SetTab = function() {
			this.TAB = this.MultiplyString(parseInt(this.Options.TabSize), this.Options.SingleTab);
		};
		QuickJSONFormatter.prototype.EnsureIsPopulated = function() {
			if (!$id(this.Options.CanvasId).innerHTML && !!this.GetRawJsonElement().value) this.Process();
		};
		QuickJSONFormatter.prototype.MultiplyString = function(num, str) {
			var sb =[];
			for (var i = 0; i < num; i++) {
				sb.push(str);
			}
			return sb.join("");
		};
		QuickJSONFormatter.prototype.SelectAll = function() {
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
				range.selectNode($id(this.Options.CanvasId));
			else if (range.moveToElementText)
				range.moveToElementText($id(this.Options.CanvasId));

			if (!!range.select)
				range.select($id(this.Options.CanvasId));
			else
				window.getSelection().addRange(range);
		};
		QuickJSONFormatter.prototype.LinkToJson = function(json) {
			var val = json || GetRawJsonElement().value;
			val = escape(val.split('/n').join(' ').split('/r').join(' '));
			var InvisibleLinkForm = document.createElement("form");
			InvisibleLinkForm.setAttribute('action',"http://www.bodurov.com/JsonFormatter/view.aspx");
			InvisibleLinkForm.setAttribute('method',"get");
			InvisibleLinkForm.setAttribute('target',"_blank");

			var InvisibleLinkUrl = document.createElement("input");
			InvisibleLinkUrl.setAttribute('name',"json");
			InvisibleLinkUrl.setAttribute('value',"{a: 1}");
			InvisibleLinkForm.appendChild(InvisibleLinkUrl);
			InvisibleLinkForm.submit();
		};

	    return QuickJSONFormatter;

	})();

}).call(this);