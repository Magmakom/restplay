let RestaurantListItem = React.createClass({
    onClick: function() {
        targetRestaurant(this.props.data.index);
    },
    render: function() {
        return (
            <button className="list-group-item" type="button" onClick={this.onClick}>
                 <span className="badge">7</span>
                 {this.props.data.name}
            </button>
        );
    }
});

let RestaurantList = React.createClass({
    getInitialState: function() {
        return {data: []};
    },

    render: function() {
      var nodes = this.state.data.map(restaurant => {
          return <RestaurantListItem data={restaurant} />;
      });
      return (
          <ul className="list-group">
              {nodes}
          </ul>
      );
    }
});

class RestaurantController {
    constructor() {
        this.restaurants = [];
    
        this.listComponent = ReactDOM.render(
            <RestaurantList />,
            document.getElementById('restaurant-list')
        );

        $.ajax({
            type: "GET",
            url: "/api/restaurant",
            data: {
                user_data: {"mod": "all"}
            },
            success: data => {
                // store absolute index
                // this hack is only needed to keep consistency with initial main.js
                // better solution would be to address targetRestaurant by real id
                data.map((restaurant, i) => restaurant.index = data[i]["_id"]); //like this?
                
                this.restaurants = data;
                this.listComponent.setState({data: data});
            },
            error: (xhr, status, error) => console.error(error.toString())
        });

        $('#search').keyup(event => this.filterByName(event.target.value));
    }

    filterByName(query) {
        let result = [];

        query = query.toLowerCase();

        this.restaurants.map(restaurant => {
            if (restaurant.name.toLowerCase().indexOf(query) > -1 || query === "") {
                result.push(restaurant);
            }
        });

        this.listComponent.setState({data: result});
    }
}

var restaurantController = new RestaurantController();